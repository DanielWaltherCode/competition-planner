package com.graphite.competitionplanner.draw.domain

import com.graphite.competitionplanner.competitioncategory.interfaces.CompetitionCategoryDTO
import com.graphite.competitionplanner.competitioncategory.interfaces.DrawType
import com.graphite.competitionplanner.competitioncategory.interfaces.GeneralSettingsDTO
import com.graphite.competitionplanner.draw.interfaces.NotEnoughRegistrationsException
import com.graphite.competitionplanner.draw.interfaces.RegistrationSeedDTO
import com.graphite.competitionplanner.draw.interfaces.Round
import com.graphite.competitionplanner.registration.interfaces.RegistrationRankingDTO
import kotlin.math.ceil
import kotlin.math.floor
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
                    DrawType.POOL_AND_CUP -> PoolAndCupDrawPolicy(competitionCategory)
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
    abstract fun createSeed(registrations: List<RegistrationRankingDTO>): List<RegistrationSeedDTO>

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

    private fun calculateNumberOfPools(numberOfRegistrations: Int, settings: GeneralSettingsDTO): Int {
        return ceil((numberOfRegistrations.toDouble() / settings.playersPerGroup.toDouble())).toInt()
    }

    private fun createEmptyPools(numberOfPools: Int): List<Pool> {
        return (1..numberOfPools).toList().map {
            Pool(it.asPoolName(), emptyList(), emptyList())
        }
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
            else -> "X" + (this % 22).asPoolName()
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

    protected fun drawPools(registrations: List<RegistrationSeedDTO>): List<Pool> {
        val numberOfPools: Int = calculateNumberOfPools(registrations.size, competitionCategory.settings)
        val pools: List<Pool> = createEmptyPools(numberOfPools)

        val seededRegistrations: List<Registration.Real> =
            registrations.filter { it.seed != null }.sortedBy { it.seed!! }.map { Registration.Real(it.registrationId) }
        val nonSeededRegistrations: List<Registration.Real> =
            registrations.filter { it.seed == null }.map { Registration.Real(it.registrationId) }

        return addRoundRobin(pools, seededRegistrations + nonSeededRegistrations.shuffled())
            .map {
                it.apply { this.matches = generateMatchesFor(this.registrationIds) }
            }
    }
}

class CupDrawPolicy(competitionCategory: CompetitionCategoryDTO) : DrawPolicy(competitionCategory) {
    override fun createDraw(registrations: List<RegistrationSeedDTO>): CompetitionCategoryDrawSpec {
        // If we are not an even power of 2, then we need to add so-called BYE players to the list of registrations
        // until we reach a number that is a power of 2
        // TODO: Do we randomize the unseeded players?
//        val seededRegistrations = registrations.filter { it.seed != null }.map { Registration.Real(it.id) }
//        val unseededRegistrations = registrations.filter { it.seed == null }.map { Registration.Real(it.id) }.shuffled()
//        val registrationsWithBye = (seededRegistrations + unseededRegistrations).tryAddByes()
        val registrationsWithBye = registrations.map { Registration.Real(it.registrationId) }.tryAddByes()
        val numberOfRounds = ceil(log2(registrationsWithBye.size.toDouble())).toInt()

        val firstRoundOfMatches = generatePlayOffMatchesForFirstRound(registrationsWithBye).map {
            it.apply {
                round = numberOfRounds.asRound()
            }
        }

        // We reverse the order of the "bottom" half of the playoff tree so that the second-best player is placed at bottom
//        firstRoundOfMatches.takeLast(firstRoundOfMatches.size / 2).reverseOrder()

        val placeholderMatches = buildRemainingPlayOffTree(firstRoundOfMatches.size / 2)

        return CupDrawSpec(1, numberOfRounds.asRound(), firstRoundOfMatches + placeholderMatches)
    }

    override fun createSeed(registrations: List<RegistrationRankingDTO>): List<RegistrationSeedDTO> {
        val sortedHighestRankFirst: List<RegistrationRankingDTO> = registrations.toList().sortedBy { -it.rank }
        val numberOfSeeds: Int = calculateNumberOfSeeds(registrations)

        return sortedHighestRankFirst.mapIndexed { index, it ->
            RegistrationSeedDTO(
                it.registrationId,
                it.competitionCategoryId,
                seed = if (index < numberOfSeeds) index + 1 else null
            )
        }
    }

    private fun calculateNumberOfSeeds(registrations: List<RegistrationRankingDTO>): Int {
        return with(registrations) {
            if (isEmpty()) 0
            else {
                when (size) {
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
        }
    }

    override fun throwExceptionIfNotEnoughRegistrations(registrations: List<RegistrationSeedDTO>) {
        if (registrations.size < 2) throw NotEnoughRegistrationsException("Failed to draw cup only. Requires at least two registrations.")
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
        swapPlayerOrder: Boolean = true
    ): List<PlayOffMatch> {
        return if (registrations.size == 2) {
            makePlayoffMatch(registrations, swapPlayerOrder)
        } else {
            val best: List<Registration> = registrations.take(2)
            val remaining: List<Registration> = registrations.drop(2)
            val first: List<PlayOffMatch> = generatePlayOffMatchesForFirstRound(listOf(best.first()) +
                    remaining.filterIndexed { index, _ -> index % 2 == 1 }, swapPlayerOrder
            )
            val second: List<PlayOffMatch> = generatePlayOffMatchesForFirstRound(listOf(best.last()) +
                    remaining.filterIndexed { index, _ -> index % 2 == 0 }, !swapPlayerOrder
            )
            (first + second.shiftOrderBy(first.size).reverseOrder()).sortedBy { it.order }
        }
    }

}

class PoolAndCupDrawPolicy(competitionCategory: CompetitionCategoryDTO) : DrawPolicy(competitionCategory) {
    override fun createDraw(registrations: List<RegistrationSeedDTO>): CompetitionCategoryDrawSpec {
        val pools: List<Pool> = drawPools(registrations)
        val playOffMatches: List<PlayOffMatch> = createPoolAndCupPlayoff(pools)
        return PoolAndCupDrawSpec(
            competitionCategory.id,
            pools,
            playOffMatches,
        )
    }

    override fun createSeed(registrations: List<RegistrationRankingDTO>): List<RegistrationSeedDTO> {
        val sortedHighestRankFirst: List<RegistrationRankingDTO> = registrations.toList().sortedBy { -it.rank }
        val numberOfSeeds: Int = calculateNumberOfSeeds(registrations)

        return sortedHighestRankFirst.mapIndexed { index, it ->
            RegistrationSeedDTO(
                it.registrationId,
                it.competitionCategoryId,
                seed = if (index < numberOfSeeds) index + 1 else null
            )
        }
    }

    private fun calculateNumberOfSeeds(registrations: List<RegistrationRankingDTO>): Int {
        return with(registrations) {
            if (isEmpty()) 0
            else floor(log2(size.toDouble())).toInt()
        }
    }

    override fun throwExceptionIfNotEnoughRegistrations(registrations: List<RegistrationSeedDTO>) {
        if ((competitionCategory.settings.playersToPlayOff == 1 && registrations.size <= competitionCategory.settings.playersPerGroup) || (registrations.size < 2)) throw NotEnoughRegistrationsException(
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
    private fun createPoolAndCupPlayoff(pools: List<Pool>): List<PlayOffMatch> {
        val placeholders: List<Registration.Placeholder> =
            pools.flatMap { group ->
                (1..competitionCategory.settings.playersToPlayOff).map { index ->
                    Registration.Placeholder(
                        group.name + index
                    )
                }
            }
                .sortedBy { it.name.reversed() } // Should result in A1, B1, C1, ..., A2, B2, C2, ...

        // If we are not an even power of 2, then we need to add so-called BYE players to the list of registrations
        // until we reach a number that is a power of 2
        val placeholdersWithBye: List<Registration> = placeholders.tryAddByes()
        val numberOfRounds: Int = ceil(log2(placeholdersWithBye.size.toDouble())).toInt()

        val firstRoundOfMatches: List<PlayOffMatch> = generatePlayOffMatchesForFirstRound(placeholdersWithBye).map {
            it.apply {
                round = numberOfRounds.asRound()
            }
        }

        // We reverse the order of the "bottom" half of the playoff tree so that the second-best player is placed at bottom
        firstRoundOfMatches.takeLast(firstRoundOfMatches.size / 2).reverseOrder()

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
        swapPlayerOrder: Boolean = true
    ): List<PlayOffMatch> {
        return if (registrations.size == 2) {
            makePlayoffMatch(registrations, swapPlayerOrder)
        } else {
            val topHalf = mutableListOf<Registration>()
            val bottomHalf = mutableListOf<Registration>()

            // Add best seeded registrations on opposite sides of play off tree
            topHalf.add(registrations.take(2).first())
            bottomHalf.add(registrations.take(2).last())
            var remaining: List<Registration> = registrations.filterNot { topHalf.contains(it) || bottomHalf.contains(it) }

            // Add winners from same pool to other side of play off tree
            topHalf.addAll(remaining.filterNot { it is Registration.Bye }
                .filter { it.toString().first() == bottomHalf.first().toString().first() })
            bottomHalf.addAll(remaining.filterNot { it is Registration.Bye }
                .filter { it.toString().first() == topHalf.first().toString().first() })
            remaining = registrations.filterNot { topHalf.contains(it) || bottomHalf.contains(it) }

            if (remaining.isNotEmpty()) {
                // Add remaining
                topHalf.addAll(
                    listOf(remaining.take(2).first()) + remaining.drop(2).filterIndexed { index, _ -> index % 2 == 0 })
                bottomHalf.addAll(
                    listOf(remaining.take(2).last()) + remaining.drop(2).filterIndexed { index, _ -> index % 2 == 1 })
            }
    //            // Add remaining
    //            topHalf.addAll(remaining.filterIndexed { index, _ -> index % 2 == 1 })
    //            bottomHalf.addAll(remaining.filterIndexed { index, _ -> index % 2 == 0 })

            topHalf.sortBy { it.toString().reversed() }
            bottomHalf.sortBy { it.toString().reversed() }

            val first = generatePlayOffMatchesForFirstRound(topHalf, swapPlayerOrder)
            val second = generatePlayOffMatchesForFirstRound(bottomHalf, !swapPlayerOrder)
            first + second.shiftOrderBy(first.size)
        }
    }
}

class PoolOnlyDrawPolicy(competitionCategory: CompetitionCategoryDTO) : DrawPolicy(competitionCategory) {

    override fun createDraw(registrations: List<RegistrationSeedDTO>): CompetitionCategoryDrawSpec {
        return PoolDrawSpec(
            competitionCategory.id,
            drawPools(registrations)
        )
    }

    override fun createSeed(registrations: List<RegistrationRankingDTO>): List<RegistrationSeedDTO> {

        val sortedHighestRankFirst: List<RegistrationRankingDTO> = registrations.toList().sortedBy { -it.rank }
        val numberOfSeeds: Int = calculateNumberOfSeeds(registrations)

        return sortedHighestRankFirst.mapIndexed { index, it ->
            RegistrationSeedDTO(
                it.registrationId,
                it.competitionCategoryId,
                seed = if (index < numberOfSeeds) index + 1 else null
            )
        }
    }

    private fun calculateNumberOfSeeds(registrations: List<RegistrationRankingDTO>): Int {
        return with(registrations) {
            if (isEmpty()) 0
            else {
                val numberOfPools = ceil((registrations.size.toDouble() / competitionCategory.settings.playersPerGroup.toDouble())).toInt()
                when (numberOfPools) {
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
    }

    override fun throwExceptionIfNotEnoughRegistrations(registrations: List<RegistrationSeedDTO>) {
        if (registrations.size < 2) throw NotEnoughRegistrationsException("Failed to draw pool only. Requires at least two registrations.")
    }
}