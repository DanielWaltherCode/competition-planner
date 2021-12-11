package com.graphite.competitionplanner.draw.domain

import com.graphite.competitionplanner.competitioncategory.domain.FindCompetitionCategory
import com.graphite.competitionplanner.competitioncategory.interfaces.DrawType
import com.graphite.competitionplanner.competitioncategory.interfaces.GeneralSettingsDTO
import com.graphite.competitionplanner.competitioncategory.entity.Round
import com.graphite.competitionplanner.competitioncategory.interfaces.CompetitionCategoryDTO
import com.graphite.competitionplanner.draw.interfaces.CompetitionCategoryDrawDTO
import com.graphite.competitionplanner.draw.interfaces.ICompetitionDrawRepository
import com.graphite.competitionplanner.draw.interfaces.ISeedRepository
import com.graphite.competitionplanner.draw.interfaces.RegistrationSeedDTO
import com.graphite.competitionplanner.registration.domain.GetRegistrationsInCompetitionCategory
import com.graphite.competitionplanner.registration.interfaces.IRegistrationRepository
import com.graphite.competitionplanner.registration.interfaces.RegistrationRankingDTO
import org.springframework.stereotype.Component
import kotlin.math.ceil
import kotlin.math.log2
import kotlin.math.pow

@Component
class CreateDraw(
    val getRegistrationsInCompetitionCategory: GetRegistrationsInCompetitionCategory,
    val findCompetitionCategory: FindCompetitionCategory,
    val createSeed: CreateSeed,
    val repository: IRegistrationRepository,
    val seedRepository: ISeedRepository,
    val drawRepository: ICompetitionDrawRepository,
) {

    /**
     * Creates a draw for the given competition category
     */
    fun execute(competitionCategoryId: Int): CompetitionCategoryDrawDTO {
        val competitionCategory: CompetitionCategoryDTO = findCompetitionCategory.byId(competitionCategoryId)

        // TODO: We should check the state of this competition category.
        // TODO: Has drawn been made? Has a match already been played? Etc.

        val registrationRankings: List<RegistrationRankingDTO> = repository.getRegistrationRanking(competitionCategory)
        val registrationsWithSeeds: List<RegistrationSeedDTO> = createSeed.execute(registrationRankings)
        seedRepository.setSeeds(registrationsWithSeeds)

        val spec = competitionCategory.settings.let {
            when (it.drawType) {
                DrawType.POOL_ONLY -> PoolDrawSpec(
                    competitionCategory.id,
                    drawPools(registrationsWithSeeds, it),
                )
                DrawType.POOL_AND_CUP -> createPoolAndCupDrawSpec(competitionCategoryId, registrationsWithSeeds, it)
                DrawType.CUP_ONLY -> createCupOnlyPlayOff(registrationsWithSeeds)
            }
        }

        return drawRepository.store(spec)
    }

    private fun drawPools(registrations: List<RegistrationSeedDTO>, settings: GeneralSettingsDTO): List<Pool> {
        val numberOfPools: Int = calculateNumberOfPools(registrations.size, settings)
        val pools: List<Pool> = createEmptyPools(numberOfPools)

        val seededRegistrations: List<Registration.Real> =
            registrations.filter { it.seed != null }.sortedBy { it.seed!! }.map { Registration.Real(it.registrationId) }
        val nonSeededRegistrations: List<Registration.Real> = registrations.filter { it.seed == null }.map { Registration.Real(it.registrationId) }

        return addRoundRobin(pools, seededRegistrations + nonSeededRegistrations.shuffled())
            .map {
                it.apply { this.matches = generateMatchesFor(this.registrationIds) }
            }
    }

    private fun createPoolAndCupDrawSpec(
        competitionCategoryId: Int,
        registrationsWithSeeds: List<RegistrationSeedDTO>,
        generalSettingsSpec: GeneralSettingsDTO
    ): PoolAndCupDrawSpec {
        val pools: List<Pool> = drawPools(registrationsWithSeeds, generalSettingsSpec)
        val playOffMatches: List<PlayOffMatch> = createPoolAndCupPlayoff(pools, generalSettingsSpec)
        return PoolAndCupDrawSpec(
            competitionCategoryId,
            pools,
            playOffMatches,
            emptyList()
        )
    }

    /**
     * Returns a list of play off matches where the name of the registrations in the first round has the name of the
     * pool positions. E.g. a match with two placeholder matches where the name is A1 and B1 would mean that position
     * 1 in pool A would go up against position 1 in pool B when pool play is over and play off is starting.
     *
     * @return A list of play off matches
     */
    private fun createPoolAndCupPlayoff(
        pools: List<Pool>,
        settings: GeneralSettingsDTO
    ): List<PlayOffMatch> {
        val placeholders: List<Registration.Placeholder> =
            pools.flatMap { group -> (1..settings.playersToPlayOff).map { index -> Registration.Placeholder(group.name + index) } }
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
        return firstRoundOfMatches + buildRemainingPlayOffTree(firstRoundOfMatches.size / 2)
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

    private fun createCupOnlyPlayOff(registrations: List<RegistrationSeedDTO>): CupDrawSpec {
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

        val placeholderMatches = buildRemainingPlayOffTree(firstRoundOfMatches.size / 2)

        return CupDrawSpec(1, numberOfRounds.asRound(), firstRoundOfMatches + placeholderMatches)
    }

    /**
     * Tries to add bye registrations until we hit a power of two registrations.
     *
     * @return A list with the original registrations plus additional bye registrations
     */
    private fun List<Registration>.tryAddByes(): List<Registration> {
        val numberOfRounds = ceil(log2(this.size.toDouble())).toInt()
        val numberOfByePlayers = (2.0.pow(numberOfRounds.toDouble()) - this.size).toInt()
        return this + (1..numberOfByePlayers).map { Registration.Bye }
    }

    /**
     * Generates the first round of matches in a play off given a list of registrations.
     *
     * The following properties are true for the generated matches:
     * - Match order is set so it guarantees that the best and second-best players do not meet until final round,
     * - Best players are paired against the worse ranked players, where BYE is considered the worst ranked player giving
     * the best players a free game in first round.
     *
     * There are two requirements on the list of registration ids:
     * 1. It has to be sorted from best to worst (given some ranking)
     * 2. The size of the list has to be an even power of 2 i.e. 0, 2, 4, 8, 16 etc.
     *
     * @return A list of matches representing the first round in a play off
     */
    private fun generatePlayOffMatchesForFirstRound(registrations: List<Registration>): List<PlayOffMatch> {
        return if (registrations.size == 2) {
            listOf(
                PlayOffMatch(
                    registrations.first(),
                    registrations.last(),
                    1,
                    Round.UNKNOWN
                )
            )
        } else {
            val best: List<Registration> = registrations.take(2)
            val remaining: List<Registration> = registrations.drop(2)
            val first: List<PlayOffMatch> = generatePlayOffMatchesForFirstRound(listOf(best.first()) +
                    remaining.filterIndexed { index, _ -> index % 2 == 1 })
            val second: List<PlayOffMatch> = generatePlayOffMatchesForFirstRound(listOf(best.last()) +
                    remaining.filterIndexed { index, _ -> index % 2 == 0 })
            first + second.shiftOrderBy(first.size)
        }
    }

    fun List<PlayOffMatch>.shiftOrderBy(n: Int): List<PlayOffMatch> {
        return this.map { it.apply { order += n } }
    }

    /**
     * Returns a list of play off matches with specified rounds and match order. This algorithm starts at the bottom
     * of the play of tree and generates all the remaining rounds recursively up until the final round.
     *
     * @param numberOfMatchesInRound - Initial number of matches to start
     */
    fun buildRemainingPlayOffTree(numberOfMatchesInRound: Int): List<PlayOffMatch> {
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
            12 -> "M"
            13 -> "N"
            14 -> "O"
            15 -> "P"
            16 -> "Q"
            17 -> "R"
            18 -> "S"
            19 -> "T"
            20 -> "U"
            21 -> "V"
            22 -> "W"
            else -> ""
        }
    }

    /**
     * Convert an integer to a Round. 1 -> FINAL, 2 -> SEMI_FINAL etc.
     */
    private fun Int.asRound(): Round {
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

}

sealed class CompetitionCategoryDrawSpec(
    val competitionCategoryId: Int
)

class CupDrawSpec(
    competitionCategoryId: Int,
    val startingRound: Round,
    val matches: List<PlayOffMatch>
) : CompetitionCategoryDrawSpec(competitionCategoryId)

class PoolDrawSpec(
    competitionCategoryId: Int,
    val pools: List<Pool>
) : CompetitionCategoryDrawSpec(competitionCategoryId)

class PoolAndCupDrawSpec(
    competitionCategoryId: Int,
    val pools: List<Pool>,
    val matches: List<PlayOffMatch> = emptyList(),
    val poolToPlayoffMap: List<PoolToPlayoffSpec>
) : CompetitionCategoryDrawSpec(competitionCategoryId)

data class Pool(
    val name: String,
    val registrationIds: List<Registration.Real>,
    var matches: List<PoolMatch>
)

data class PlayOffMatch(
    val registrationOneId: Registration,
    val registrationTwoId: Registration,
    var order: Int,
    var round: Round
)

data class PoolToPlayoffSpec(
    val pool: Pool,
    val position: Int,
    val playOffMatch: PlayOffMatch,
    val playOffPosition: Int
)

sealed class Registration {
    class Real(val id: Int) : Registration() {
        override fun toString(): String {
            return id.toString()
        }
    }

    class Placeholder(var name: String = "Placeholder") : Registration() {
        override fun toString(): String {
            return name
        }
    }

    object Bye : Registration() {
        override fun toString(): String {
            return "BYE"
        }
    }
}

data class PoolMatch(
    val registrationOneId: Registration.Real,
    val registrationTwoId: Registration.Real
)

