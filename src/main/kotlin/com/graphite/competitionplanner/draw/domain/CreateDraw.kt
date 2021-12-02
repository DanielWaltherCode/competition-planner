package com.graphite.competitionplanner.draw.domain

import com.graphite.competitionplanner.competitioncategory.domain.FindCompetitionCategory
import com.graphite.competitionplanner.competitioncategory.interfaces.DrawType
import com.graphite.competitionplanner.competitioncategory.interfaces.GeneralSettingsSpec
import com.graphite.competitionplanner.competitioncategory.entity.Round
import com.graphite.competitionplanner.draw.interfaces.CompetitionCategoryDrawDTO
import com.graphite.competitionplanner.draw.interfaces.ICompetitionDrawRepository
import com.graphite.competitionplanner.draw.interfaces.ISeedRepository
import com.graphite.competitionplanner.draw.interfaces.RegistrationSeedDTO
import com.graphite.competitionplanner.registration.domain.GetRegistrationsInCompetitionCategory
import com.graphite.competitionplanner.registration.interfaces.IRegistrationRepository
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
    val drawRepository: ICompetitionDrawRepository
) {

    /**
     * Creates a draw for the given competition category
     */
    fun execute(competitionCategoryId: Int): CompetitionCategoryDrawDTO {
        val competitionCategory = findCompetitionCategory.byId(competitionCategoryId)

        // TODO: We should check the state of this competition category.
        // TODO: Has drawn been made? Has a match already been played? Etc.

        val registrationRanks = repository.getRegistrationRank(competitionCategory)
        val registrationsWithSeeds = createSeed.execute(registrationRanks)
        seedRepository.setSeeds(registrationsWithSeeds)

        val spec = competitionCategory.settings.let {
            when (it.drawType) {
                DrawType.POOL_ONLY -> PoolDrawSpec(
                    competitionCategory.id,
                    drawPools(registrationsWithSeeds, it),
                )
                DrawType.POOL_AND_CUP -> createPoolAndCupDrawSpec(competitionCategoryId, registrationsWithSeeds, it)
                DrawType.CUP_ONLY -> createPlayOffs(registrationsWithSeeds)
            }
        }

        return drawRepository.store(spec)
    }

    private fun drawPools(registrations: List<RegistrationSeedDTO>, settings: GeneralSettingsSpec): List<Pool> {
        val numberOfPools = calculateNumberOfPools(registrations.size, settings)
        val pools = createEmptyPools(numberOfPools)

        val seededRegistrations =
            registrations.filter { it.seed != null }.sortedBy { it.seed!! }.map { Registration.Real(it.id) }
        val nonSeededRegistrations = registrations.filter { it.seed == null }.map { Registration.Real(it.id) }

        return addRoundRobin(pools, seededRegistrations + nonSeededRegistrations.shuffled())
            .map {
                it.apply { this.matches = generateMatchesFor(this.registrationIds) }
            }
    }

    private fun createPoolAndCupDrawSpec(
        competitionCategoryId: Int,
        registrationsWithSeeds: List<RegistrationSeedDTO>,
        generalSettingsSpec: GeneralSettingsSpec
    ): PoolAndCupDrawSpec {
        val pools = drawPools(registrationsWithSeeds, generalSettingsSpec)
        val playOff = createPlayOffWithOnlyPlaceholder(registrationsWithSeeds, generalSettingsSpec)
        val poolToPlayoffSpec = createPoolToPlayOffMap(pools, playOff, generalSettingsSpec)
        return PoolAndCupDrawSpec(
            competitionCategoryId,
            pools,
            playOff,
            poolToPlayoffSpec
        )
    }

    private fun createPoolToPlayOffMap(
        pools: List<Pool>,
        playOff: List<PlayOffMatch>,
        generalSettingsSpec: GeneralSettingsSpec
    ): List<PoolToPlayoffSpec> {

        val res = pools.flatMap { group -> (1 .. generalSettingsSpec.playersToPlayOff).map { index ->  FakePlaceholder(group.name + index) } }
        val fakeRegistrations = listOf<Registration>()

        return emptyList()
    }

    data class FakePlaceholder(val name: String)

    private fun createPlayOffWithOnlyPlaceholder(
        registrations: List<RegistrationSeedDTO>,
        settings: GeneralSettingsSpec
    ): List<PlayOffMatch> {
        val matchesInFirstRound = ceil(
            settings.playersToPlayOff.toDouble() * calculateNumberOfPools(
                registrations.size,
                settings
            ).toDouble() / 2.0
        ).toInt()
        return buildRemainingPlayOffTree(matchesInFirstRound)
    }

    private fun calculateNumberOfPools(numberOfRegistrations: Int, settings: GeneralSettingsSpec): Int {
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
                val first = registrations.first()
                val pool = addRegistrationToPool(pools.first(), first)
                val remaining = registrations.takeLast(registrations.size - 1)
                listOf(pool) + addRoundRobin(pools.takeLast(pools.size - 1), remaining)
            } else {
                val round = addRoundRobin(pools, registrations.take(pools.size))
                addRoundRobin(round, registrations.takeLast(registrations.size - pools.size))
            }
        }
    }

    private fun addRegistrationToPool(pool: Pool, registration: Registration.Real): Pool {
        return Pool(pool.name, pool.registrationIds + listOf(registration), pool.matches)
    }

    private fun createPlayOffs(registrations: List<RegistrationSeedDTO>): CupDrawSpec {
        // If we are not an even power of 2, then we need to add so called BYE players to the list of registrations
        // until we reach a number that is a power of 2
        val numberOfRounds = ceil(log2(registrations.size.toDouble())).toInt()
        val numberOfByePlayers = (2.0.pow(numberOfRounds.toDouble()) - registrations.size).toInt()
        val registrationsWithBye =
            registrations.map { Registration.Real(it.id) } + (1..numberOfByePlayers).map { Registration.Bye }

        val firstRoundOfMatches = generatePlayOffMatchesForFirstRound(registrationsWithBye).map {
            it.apply {
                round = numberOfRounds.asRound()
            }
        }

        val placeholderMatches = buildRemainingPlayOffTree(firstRoundOfMatches.size / 2)

        return CupDrawSpec(1, numberOfRounds.asRound(), firstRoundOfMatches + placeholderMatches)
    }

    /**
     * Generates the first round of matches in a play off given a list of registrations.
     *
     * The following properties are true for the generated matches:
     * - Match order is set so it guarantees that the best and second best players do not meet until final round,
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
            val best = registrations.take(2)
            val remaining = registrations.drop(2)
            val first = generatePlayOffMatchesForFirstRound(listOf(best.first()) +
                    remaining.filterIndexed { index, _ -> index % 2 == 1 })
            val second = generatePlayOffMatchesForFirstRound(listOf(best.last()) +
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
                listOf(PlayOffMatch(Registration.Placeholder, Registration.Placeholder, 1, Round.FINAL))
            }
            else -> {
                val thisRound = (1..numberOfMatchesInRound).map {
                    PlayOffMatch(
                        Registration.Placeholder,
                        Registration.Placeholder,
                        it,
                        numberOfMatchesToRound(numberOfMatchesInRound)
                    )
                }
                val nextRound = buildRemainingPlayOffTree(numberOfMatchesInRound / 2)
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

    object Placeholder : Registration() {
        override fun toString(): String {
            return "Placeholder"
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

