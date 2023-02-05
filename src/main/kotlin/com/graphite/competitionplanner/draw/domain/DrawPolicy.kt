package com.graphite.competitionplanner.draw.domain

import com.graphite.competitionplanner.common.exception.BadRequestException
import com.graphite.competitionplanner.common.exception.BadRequestType
import com.graphite.competitionplanner.competitioncategory.interfaces.CompetitionCategoryDTO
import com.graphite.competitionplanner.competitioncategory.interfaces.DrawType
import com.graphite.competitionplanner.competitioncategory.interfaces.PoolDrawStrategy
import com.graphite.competitionplanner.draw.interfaces.RegistrationSeedDTO
import com.graphite.competitionplanner.draw.interfaces.Round
import com.graphite.competitionplanner.registration.domain.Registration
import com.graphite.competitionplanner.registration.interfaces.RegistrationRankingDTO
import kotlin.math.ceil
import kotlin.math.log2
import kotlin.math.pow

sealed class DrawPolicy(
    val competitionCategory: CompetitionCategoryDTO
) {

    companion object {
        fun createDrawStrategy(competitionCategory: CompetitionCategoryDTO): DrawPolicy {
            return competitionCategory.settings.let {
                when (it.drawType) {
                    DrawType.POOL_ONLY -> PoolOnlyDrawPolicy(competitionCategory)
                    DrawType.CUP_ONLY -> CupDrawPolicy(competitionCategory)
                    DrawType.POOL_AND_CUP -> PoolAndCupDrawPolicy(PoolOnlyDrawPolicy(competitionCategory), CupDrawPolicy(competitionCategory), competitionCategory)
                    DrawType.POOL_AND_CUP_WITH_B_PLAY_OFF -> PoolAndCupWithBPlayoffsDrawPolicy(PoolOnlyDrawPolicy(competitionCategory), competitionCategory)
                }
            }
        }
    }

    abstract fun createDraw(registrations: List<RegistrationSeedDTO>): CompetitionCategoryDrawSpec

    /**
     * Seeding players means that you assign the best players who are taking part in the competition or tournament to a
     * position in an ordered list before the draw commences.
     *
     * Their positions in the list are determined by their relative playing standard - with the best player ranked at
     * number one.
     *
     * By doing this you can then distribute these players evenly throughout the draw so that they will not meet in the
     * early round of a knock-out competition or tournament.
     *
     * The number of seeds will vary according to the number of entries in the competition or tournament. However, the
     * number of seeds will always be to the power of 2, so this means you should have either 1, 2, 4, 8, 16, 32, or 64 etc.
     * seeds. All other player will not be seeded and will be randomly drawn when the draw takes place
     */
    fun createSeed(registrations: List<RegistrationRankingDTO>): List<RegistrationSeedDTO> {
        val sortedHighestRankFirst: List<RegistrationRankingDTO> = registrations.toList().sortedBy { -it.rank }
        val numberOfSeeds: Int = calculateNumberOfSeeds(registrations.size)

        return sortedHighestRankFirst.mapIndexed { index, it ->
            RegistrationSeedDTO(
                it.registration,
                it.competitionCategoryId,
                seed = if (index < numberOfSeeds) index + 1 else null
            )
        }
    }

    /**
     * Return the number of registrations that should be considered seeded
     */
    abstract fun calculateNumberOfSeeds(numberOfRegistrations: Int): Int

    /**
     * Check if there are enough registrations in the competition category.
     * @throws BadRequestException When there are not enough registrations to make a draw
     */
    abstract fun throwExceptionIfNotEnoughRegistrations(registrations: List<RegistrationSeedDTO>)

    /**
     * Returns a list of play off matches with specified rounds and match order. This algorithm starts at the bottom
     * of the play of tree and generates all the remaining rounds recursively up until the final round.
     *
     * @param numberOfMatchesInRound - Initial number of matches to start
     */
    protected fun buildRemainingPlayOffTree(numberOfMatchesInRound: Int): List<PlayOffMatch> {
        return when {
            numberOfMatchesInRound < 1 -> {
                emptyList()
            }
            numberOfMatchesInRound == 1 -> {
                listOf(PlayOffMatch(Registration.Placeholder(), Registration.Placeholder(), 1, Round.FINAL))
            }
            else -> {
                val thisRound: List<PlayOffMatch> = (1..numberOfMatchesInRound).map {
                    PlayOffMatch(
                        Registration.Placeholder(),
                        Registration.Placeholder(),
                        it,
                        numberOfMatchesToRound(numberOfMatchesInRound)
                    )
                }
                val nextRound: List<PlayOffMatch> = buildRemainingPlayOffTree(numberOfMatchesInRound / 2)
                thisRound + nextRound
            }
        }
    }

    /**
     * Converts a number of matches to a Round
     */
    private fun numberOfMatchesToRound(number: Int): Round {
        return (ceil(log2(number.toDouble())).toInt() + 1).asRound()
    }

    /**
     * Tries to add bye registrations until we hit a power of two registrations.
     *
     * @return A list with the original registrations plus additional bye registrations
     */
    protected fun List<Registration>.tryAddByes(): List<Registration> {
        val numberOfRounds = ceil(log2(this.size.toDouble())).toInt()
        val numberOfByePlayers = (2.0.pow(numberOfRounds.toDouble()) - this.size).toInt()
        return this + (1..numberOfByePlayers).map { Registration.Bye }
    }

    /**
     * Convert an integer to a Round. 1 -> FINAL, 2 -> SEMI_FINAL etc.
     */
    protected fun Int.asRound(): Round {
        return when (this) {
            1 -> Round.FINAL
            2 -> Round.SEMI_FINAL
            3 -> Round.QUARTER_FINAL
            4 -> Round.ROUND_OF_16
            5 -> Round.ROUND_OF_32
            6 -> Round.ROUND_OF_64
            7 -> Round.ROUND_OF_128
            else -> Round.UNKNOWN
        }
    }

    protected fun List<PlayOffMatch>.shiftOrderBy(n: Int): List<PlayOffMatch> {
        return this.map { it.apply { order += n } }
    }

    protected fun List<PlayOffMatch>.reverseOrder(): List<PlayOffMatch> {
        val reversedOrder = this.map { it.order }.reversed()
        return this.mapIndexed { index, match -> match.apply { order = reversedOrder[index] } }
    }

    protected fun makePlayoffMatch(registrations: List<Registration>, swapPlayerOrder: Boolean): List<PlayOffMatch> {
        // We have to swap order of players if we are in the bottom half of the play off tree.
        // E.g. A1 is placed as registration 1, while B1 will be registration 2
        val best = if (swapPlayerOrder) registrations.first() else registrations.last()
        val worst = if (swapPlayerOrder) registrations.last() else registrations.first()
        return listOf(
            PlayOffMatch(
                best,
                worst,
                1,
                Round.UNKNOWN
            )
        )
    }


}

class CupDrawPolicy(competitionCategory: CompetitionCategoryDTO) : DrawPolicy(competitionCategory) {

    override fun createDraw(registrations: List<RegistrationSeedDTO>): CompetitionCategoryDrawSpec {
        // If we are not an even power of 2, then we need to add so-called BYE players to the list of registrations
        // until we reach a number that is a power of 2
        val seededRegistrations = registrations.filter { it.seed != null }.map { it.registration }.shuffleSeeded()
        val unseededRegistrations = registrations.filter { it.seed == null }.map { it.registration }.shuffled()
        val registrationsWithBye = (seededRegistrations + unseededRegistrations).tryAddByes()
        val numberOfRounds = ceil(log2(registrationsWithBye.size.toDouble())).toInt()

        val firstRoundOfMatches = generatePlayOffMatchesForFirstRound(registrationsWithBye).map {
            it.apply {
                round = numberOfRounds.asRound()
            }
        }

        val placeholderMatches = buildRemainingPlayOffTree(firstRoundOfMatches.size / 2)

        val byeMatches = firstRoundOfMatches.filter {
            it.registrationOneId == Registration.Bye || it.registrationTwoId == Registration.Bye }

        if (byeMatches.isNotEmpty()) {
            val secondRound = (numberOfRounds-1).asRound()
            val nextRoundMatches = placeholderMatches.filter { it.round == secondRound }
            moveRealRegistrationsToNextRound(byeMatches, nextRoundMatches)
        }

        return CupDrawSpec(competitionCategory.id, firstRoundOfMatches + placeholderMatches)
    }

    private fun moveRealRegistrationsToNextRound(byeMatches: List<PlayOffMatch>, nextRoundMatches: List<PlayOffMatch>) {
        byeMatches.forEach {
            val nextOrderNumber = ceil( it.order / 2.0 ).toInt() // 1 -> 1, 2 -> 1, 3 -> 2, etc.
            if (it.registrationOneId is Registration.Real) {
                nextRoundMatches.first { match -> match.order == nextOrderNumber }.registrationOneId = it.registrationOneId
                it.winner = it.registrationOneId
            } else {
                nextRoundMatches.first { match -> match.order == nextOrderNumber}.registrationTwoId = it.registrationTwoId
                it.winner = it.registrationTwoId
            }
        }
    }

    override fun calculateNumberOfSeeds(numberOfRegistrations: Int): Int {
        return when (numberOfRegistrations) {
                in 0 .. 1 -> {
                    0
                }
                in 2..15 -> {
                    2
                }
                in 16..31 -> {
                    4
                }
                in 32..63 -> {
                    8
                }
                in 64..127 -> {
                    16
                }
                else -> {
                    32
                }
            }
    }

    override fun throwExceptionIfNotEnoughRegistrations(registrations: List<RegistrationSeedDTO>) {
        if (registrations.size < 2) throw BadRequestException(BadRequestType.DRAW_NOT_ENOUGH_REGISTRATIONS, "Failed to draw cup only. Requires at least two registrations.")
    }

    private fun List<Registration>.shuffleSeeded(): List<Registration> {
        return if (this.size == 2) {
            this
        } else {
            this.take(this.size / 2).shuffleSeeded() + this.drop( this.size / 2 ).shuffled()
        }
    }

    /**
     * Generates the first round of matches in a play off given a list of registrations.
     *
     * The following properties are true for the generated matches:
     * - Match order is set, so it guarantees that the best and second-best players do not meet until final round,
     * - Best players are paired against the worse ranked players, where BYE is considered the worst ranked player giving
     * the best players a free game in first round.
     *
     * There are two requirements on the list of registration ids:
     * 1. It has to be sorted from best to worst (given some ranking)
     * 2. The size of the list has to be an even power of 2 i.e. 0, 2, 4, 8, 16 etc.
     *
     * @return A list of matches representing the first round in a play off
     */
    fun generatePlayOffMatchesForFirstRound(
        registrations: List<Registration>,
        swapPlayerOrder: Boolean = true
    ): List<PlayOffMatch> {
        return if (registrations.size == 2) {
            makePlayoffMatch(registrations, swapPlayerOrder)
        } else {
            val best: List<Registration> = registrations.take(2)
            val remaining: List<Registration> = registrations.drop(2)
            val (byes, rest) = remaining.partition { it is Registration.Bye }
            val first: List<PlayOffMatch>
            val second: List<PlayOffMatch>

            if (byes.size == 3 &&
                registrations.size == 8 &&
                registrations.count { it.toString().last() == '1' && it is Registration.Placeholder} == byes.size)
            {
                // There seems to be a special case when we have 8 registrations and out of those are:
                // 3 pool winners, 3 Byes, and 2 pool second placers. The general algorithm (see else-case below)
                // does not seem to distribute BYEs good enough. I mean, the half that gets two winners should
                // also get two BYEs because winners should be matched against BYEs before any second place winner.

                // Partition the rest in pool winners and pool seconds
                val (p1, p2) = rest.partition { it.toString().last() == '1' }

                first = generatePlayOffMatchesForFirstRound(listOf(best.first()) + p1 + byes.take(2), swapPlayerOrder)
                second = generatePlayOffMatchesForFirstRound(listOf(best.last()) + p2 + byes.takeLast(1), !swapPlayerOrder)

            } else {
                first = generatePlayOffMatchesForFirstRound(listOf(best.first()) +
                        remaining.filterIndexed { index, _ -> index % 2 == 1 }, swapPlayerOrder
                )
                second = generatePlayOffMatchesForFirstRound(listOf(best.last()) +
                        remaining.filterIndexed { index, _ -> index % 2 == 0 }, !swapPlayerOrder
                )
            }
            (first + second.shiftOrderBy(first.size).reverseOrder()).sortedBy { it.order }
        }
    }

}

class PoolAndCupDrawPolicy(
    private val poolDrawPolicy: PoolOnlyDrawPolicy,
    private val cupDrawPolicy: CupDrawPolicy,
    competitionCategory: CompetitionCategoryDTO
) : DrawPolicy(competitionCategory) {

    override fun createDraw(registrations: List<RegistrationSeedDTO>): CompetitionCategoryDrawSpec {
        val pools: List<Pool> = (poolDrawPolicy.createDraw(registrations) as PoolDrawSpec).pools

        val numberOfSeededRegistrationsInPlayOff = when (pools.size) {
            // Only consider number of pools when determining number of seeds in play off, even if snake-draw.
            in 0 .. 1 -> {
                0
            }
            in 2..3 -> {
                2
            }
            in 4..7 -> {
                4
            }
            in 8..15 -> {
                8
            }
            else -> {
                16
            }
        }

        val playOffMatches: List<PlayOffMatch> = createPoolAndCupPlayoff(pools, numberOfSeededRegistrationsInPlayOff)
        return PoolAndCupDrawSpec(
            competitionCategory.id,
            pools,
            playOffMatches,
        )
    }

    override fun calculateNumberOfSeeds(numberOfRegistrations: Int): Int {
        return poolDrawPolicy.calculateNumberOfSeeds(numberOfRegistrations)
    }

    override fun throwExceptionIfNotEnoughRegistrations(registrations: List<RegistrationSeedDTO>) {
        if ((competitionCategory.settings.playersToPlayOff == 1
                        && registrations.size <= competitionCategory.settings.playersPerGroup)
                || (registrations.size < 2)) throw BadRequestException(BadRequestType.DRAW_NOT_ENOUGH_REGISTRATIONS,
            "Failed to draw pool and cup. Too few people would have advanced to playoff."
        )
    }

    /**
     * Returns a list of play off matches where the name of the registrations in the first round has the name of the
     * pool positions. E.g. a match with two placeholder matches where the name is A1 and B1 would mean that position
     * 1 in pool A would go up against position 1 in pool B when pool play is over and play off is starting.
     *
     * @return A list of play off matches
     */
    private fun createPoolAndCupPlayoff(pools: List<Pool>, numberOfSeeded: Int): List<PlayOffMatch> {
        val placeholders: List<Registration.Placeholder> =
            pools.flatMap { group ->
                (1..competitionCategory.settings.playersToPlayOff).map { index ->
                    Registration.Placeholder(
                        group.name + index
                    )
                }
            }.sortedBy { it.name.reversed() } // Should result in A1, B1, C1, ..., A2, B2, C2, ...

        // If we are not an even power of 2, then we need to add so-called BYE players to the list of registrations
        // until we reach a number that is a power of 2
        val placeholdersWithBye: List<Registration> = placeholders.tryAddByes()
        val numberOfRounds: Int = ceil(log2(placeholdersWithBye.size.toDouble())).toInt()

        val firstRoundOfMatches: List<PlayOffMatch> = generatePlayOffMatchesForFirstRound(placeholdersWithBye, numberOfSeeded).map {
            it.apply {
                round = numberOfRounds.asRound()
            }
        }

        return firstRoundOfMatches + buildRemainingPlayOffTree(firstRoundOfMatches.size / 2)
    }

    /**
     * Generates the first round of matches in a play off given a list of registrations.
     *
     * The following properties are true for the generated matches:
     * - Match order is set, so it guarantees that the best and second-best players do not meet until final round,
     * - Best players are paired against the worse ranked players, where BYE is considered the worst ranked player giving
     * the best players a free game in first round.
     *
     * There are two requirements on the list of registration ids:
     * 1. It has to be sorted from best to worst (given some ranking)
     * 2. The size of the list has to be an even power of 2 i.e. 0, 2, 4, 8, 16 etc.
     *
     * @return A list of matches representing the first round in a play off
     */
    private fun generatePlayOffMatchesForFirstRound(
        registrations: List<Registration>,
        numberOfSeeded: Int,
        swapPlayerOrder: Boolean = true
    ): List<PlayOffMatch> {
        return if (registrations.size == 2) {
            makePlayoffMatch(registrations, swapPlayerOrder)
        } else {
            val (topHalf, bottomHalf) = distributeSeededInDifferentHalves(registrations, numberOfSeeded)
                .placeWinnersFromSamePoolOnOppositeSides()
                .splitRemainingWinnersOnDifferentHalves()
                .uniformlyDistributeByesOnHalves()

            val first = cupDrawPolicy.generatePlayOffMatchesForFirstRound(topHalf, swapPlayerOrder = swapPlayerOrder)
            val second = cupDrawPolicy.generatePlayOffMatchesForFirstRound(bottomHalf, swapPlayerOrder = !swapPlayerOrder)
            (first + second.shiftOrderBy(first.size).reverseOrder()).sortedBy { it.order }
        }
    }

    private fun Halves.uniformlyDistributeByesOnHalves(): Halves {
        return if (this.remaining.isEmpty()) {
            this
        }else {
            if (this.topHalf.size < this.bottomHalf.size) {
                Halves(
                    this.topHalf + this.remaining.take(1),
                    this.bottomHalf,
                    this.remaining.drop(1)
                ).uniformlyDistributeByesOnHalves()
            } else {
                Halves(
                    this.topHalf,
                    this.bottomHalf + this.remaining.take(1),
                    this.remaining.drop(1)
                ).uniformlyDistributeByesOnHalves()
            }
        }
    }

    private fun List<Registration>.findWinnersFromSamePool(winners: List<Registration>): List<Registration> {
        val groupPrefixes = winners.map { it.toString().dropLast(1) }
        return this.filterNot { it is Registration.Bye }.filter { groupPrefixes.contains(it.toString().dropLast(1)) }
    }

    private fun Halves.splitRemainingWinnersOnDifferentHalves(): Halves {
        return if (this.remaining.isEmpty() || this.remaining.all { it is Registration.Bye }) {
            Halves(this.topHalf, this.bottomHalf, this.remaining)
        }else {
            if (this.topHalf.size < this.bottomHalf.size) {
                Halves(
                    this.topHalf + this.remaining.take(1),
                    this.bottomHalf,
                    this.remaining.drop(1)
                ).splitRemainingWinnersOnDifferentHalves()
            } else {
                Halves(
                    this.topHalf,
                    this.bottomHalf + this.remaining.take(1),
                    this.remaining.drop(1)
                ).splitRemainingWinnersOnDifferentHalves()
            }
        }
    }

    private fun Halves.placeWinnersFromSamePoolOnOppositeSides(): Halves {
        val topHalf = this.topHalf + this.remaining.findWinnersFromSamePool(this.bottomHalf)
        val bottomHalf = this.bottomHalf + this.remaining.findWinnersFromSamePool(this.topHalf)
        val remaining = this.remaining.filterNot { topHalf.contains(it) || bottomHalf.contains(it) }
        return Halves(
            topHalf,
            bottomHalf,
            remaining
        )
    }

    private fun distributeSeededInDifferentHalves(registrations: List<Registration>, numberOfSeeded: Int): Halves {
        return if (registrations.size == 4 ) {
            Halves(
                listOf(registrations[0], registrations[3]),
                listOf(registrations[1], registrations[2]),
                emptyList()
            )
        } else {
            val seeded = registrations.take(numberOfSeeded).shuffleSeeded()
            val topTwo = seeded.take(2)
            val rest = seeded.drop(2)
            val remaining = registrations.drop(numberOfSeeded)
            val topHalf = listOf(topTwo.first()) + rest.filterIndexed { index, _ -> index % 2 == 1 }
            val bottomHalf = listOf(topTwo.last()) + rest.filterIndexed { index, _ -> index % 2 == 0 }
            Halves(topHalf, bottomHalf, remaining)
        }
    }

    private fun List<Registration>.shuffleSeeded(): List<Registration> {
        return if (this.size == 2) {
            this
        } else {
            this.take(this.size / 2).shuffleSeeded() + this.drop( this.size / 2 ).shuffled()
        }
    }

    data class Halves(
        val topHalf: List<Registration>,
        val bottomHalf: List<Registration>,
        val remaining: List<Registration>
    )
}

class PoolOnlyDrawPolicy(competitionCategory: CompetitionCategoryDTO) : DrawPolicy(competitionCategory) {

    override fun createDraw(registrations: List<RegistrationSeedDTO>): CompetitionCategoryDrawSpec {
        return PoolDrawSpec(
            competitionCategory.id,
            drawPools(registrations)
        )
    }

    override fun calculateNumberOfSeeds(numberOfRegistrations: Int): Int {
        return when(this.competitionCategory.settings.poolDrawStrategy) {
            PoolDrawStrategy.SNAKE -> numberOfRegistrations
            PoolDrawStrategy.NORMAL -> when (numberOfPools(numberOfRegistrations)) {
                in 0 .. 1 -> {
                    0
                }
                in 2..3 -> {
                    2
                }
                in 4..7 -> {
                    4
                }
                in 8..15 -> {
                    8
                }
                else -> {
                    16
                }
            }
        }
    }

    override fun throwExceptionIfNotEnoughRegistrations(registrations: List<RegistrationSeedDTO>) {
        if (registrations.size < 2) throw BadRequestException(BadRequestType.DRAW_NOT_ENOUGH_REGISTRATIONS, "Failed to draw pool only. Requires at least two registrations.")
    }

    private fun drawPools(registrations: List<RegistrationSeedDTO>): List<Pool> {
        val numberOfPools: Int = numberOfPools(registrations.size)
        val pools: List<Pool> = createEmptyPools(numberOfPools)

        val seededRegistrations: List<Registration.Real> =
            registrations.filter { it.seed != null }.sortedBy { it.seed!! }.map { it.registration }
        val nonSeededRegistrations: List<Registration.Real> =
            registrations.filter { it.seed == null }.map { it.registration }

        return when(this.competitionCategory.settings.poolDrawStrategy) {
            PoolDrawStrategy.SNAKE -> addSnakeWay(pools, seededRegistrations)
            PoolDrawStrategy.NORMAL -> addRoundRobin(pools, seededRegistrations + nonSeededRegistrations.shuffled())
        }.map {
            it.apply { this.matches = generateMatchesFor(this.registrationIds) }
        }
    }

    private fun createEmptyPools(numberOfPools: Int): List<Pool> {
        return (1..numberOfPools).toList().map {
            Pool(it.asPoolName(), emptyList(), emptyList())
        }
    }

    private fun numberOfPools(numberOfRegistrations: Int): Int {
        return ceil((numberOfRegistrations.toDouble() / competitionCategory.settings.playersPerGroup.toDouble())).toInt()
    }

    /**
     * Convert an integer to a pool name. 1 -> A, 2 -> B, ..., 22 -> W
     */
    private fun Int.asPoolName(): String {
        return when (this) {
            1 -> "A"
            2 -> "B"
            3 -> "C"
            4 -> "D"
            5 -> "E"
            6 -> "F"
            7 -> "G"
            8 -> "H"
            9 -> "I"
            10 -> "J"
            11 -> "K"
            12 -> "L"
            13 -> "M"
            14 -> "N"
            15 -> "O"
            16 -> "P"
            17 -> "Q"
            18 -> "R"
            19 -> "S"
            20 -> "T"
            21 -> "U"
            22 -> "V"
            else -> {
                val s = "X"
                val t = (this - 22).asPoolName()
                s + t
            }
        }
    }

    /**
     * Adds a set of registrations in a round-robin fashion to a set of pools. The first registration is added to
     * the first pool, then the second registration is added to the second pool and so on. When all pools have
     * received their first round of registration, the process repeats.
     */
    private fun addRoundRobin(pools: List<Pool>, registrations: List<Registration.Real>): List<Pool> {
        return if (registrations.isEmpty()) {
            pools
        } else {
            return if (registrations.size <= pools.size) {
                val first: Registration.Real = registrations.first()
                val pool: Pool = addRegistrationToPool(pools.first(), first)
                val remaining: List<Registration.Real> = registrations.takeLast(registrations.size - 1)
                listOf(pool) + addRoundRobin(pools.takeLast(pools.size - 1), remaining)
            } else {
                val round: List<Pool> = addRoundRobin(pools, registrations.take(pools.size))
                addRoundRobin(round, registrations.takeLast(registrations.size - pools.size))
            }
        }
    }

    /**
     * Adds a set of registrations to a set of pools in a snake-way. The first round of registrations is added,
     * left to right, then the second pool player is added right to left, and so on until all registrations are placed
     * in pools.
     */
    private fun addSnakeWay(
        pools: List<Pool>,
        registrations: List<Registration.Real>,
        direction: Direction = Direction.LEFT_TO_RIGHT
    ): List<Pool> {
        return if (registrations.isEmpty()) {
            pools
        } else {
            return if (registrations.size <= pools.size) {
                when(direction) {
                    Direction.LEFT_TO_RIGHT -> {
                        val first: Registration.Real = registrations.first()
                        val pool: Pool = addRegistrationToPool(pools.first(), first)
                        val remaining: List<Registration.Real> = registrations.takeLast(registrations.size - 1)
                        listOf(pool) + addSnakeWay(pools.takeLast(pools.size - 1), remaining, direction)
                    }
                    Direction.RIGHT_TO_LEFT -> {
                        val first: Registration.Real = registrations.first()
                        val pool: Pool = addRegistrationToPool(pools.last(), first)
                        val remaining: List<Registration.Real> = registrations.takeLast(registrations.size - 1)
                        addSnakeWay(pools.take(pools.size - 1), remaining, direction) + listOf(pool)
                    }
                }
            } else {
                val round: List<Pool> = addSnakeWay(pools, registrations.take(pools.size), direction)
                addSnakeWay(round, registrations.takeLast(registrations.size - pools.size), direction.opposite())
            }
        }
    }

    enum class Direction {
        LEFT_TO_RIGHT, RIGHT_TO_LEFT
    }

    private fun Direction.opposite(): Direction {
        return when(this) {
            Direction.LEFT_TO_RIGHT -> Direction.RIGHT_TO_LEFT
            Direction.RIGHT_TO_LEFT -> Direction.LEFT_TO_RIGHT
        }
    }

    private fun addRegistrationToPool(pool: Pool, registration: Registration.Real): Pool {
        return Pool(pool.name, pool.registrationIds + listOf(registration), pool.matches)
    }

    /**
     * Generate a list of matches where every registration in the list will go up against every other registration
     * exactly once. Example a list of registration 1, 2, 3 would result in the match ups 1 - 2, 1 - 3, and 2 - 3
     */
    private fun generateMatchesFor(registrations: List<Registration.Real>): List<PoolMatch> {
        with(registrations) {
            return if (isEmpty()) {
                emptyList()
            } else {
                val remaining = takeLast(registrations.size - 1)
                val matches = generateMatchesFor(first(), remaining)
                matches + generateMatchesFor(remaining)
            }
        }
    }

    /**
     * Returns a list of matches where each mach is between the given registration and one other in the other lists.
     *
     * Example: Given registration id is 1, and others contain ids 2, 3, 4. Then returned matches are:
     * 1 - 2, 1 - 3, and 1 - 4
     */
    private fun generateMatchesFor(registration: Registration.Real, others: List<Registration.Real>): List<PoolMatch> {
        return (1..others.size).map { registration }.zip(others).map { PoolMatch(it.first, it.second) }
    }
}

class PoolAndCupWithBPlayoffsDrawPolicy(
    private val poolDrawPolicy: PoolOnlyDrawPolicy,
    competitionCategory: CompetitionCategoryDTO
) : DrawPolicy(competitionCategory) {
    override fun createDraw(registrations: List<RegistrationSeedDTO>): CompetitionCategoryDrawSpec {
        val pools: List<Pool> = (poolDrawPolicy.createDraw(registrations) as PoolDrawSpec).pools
        return PoolAndCupDrawWithBPlayoffSpec(competitionCategory.id, pools)
    }

    override fun calculateNumberOfSeeds(numberOfRegistrations: Int): Int {
        return poolDrawPolicy.calculateNumberOfSeeds(numberOfRegistrations)
    }

    override fun throwExceptionIfNotEnoughRegistrations(registrations: List<RegistrationSeedDTO>) {
        TODO("Not yet implemented")
    }

}