package com.graphite.competitionplanner.draw.domain

import com.graphite.competitionplanner.competitioncategory.domain.FindCompetitionCategory
import com.graphite.competitionplanner.competitioncategory.interfaces.DrawType
import com.graphite.competitionplanner.competitioncategory.interfaces.GeneralSettingsSpec
import com.graphite.competitionplanner.draw.interfaces.ISeedRepository
import com.graphite.competitionplanner.draw.interfaces.RegistrationSeedDTO
import com.graphite.competitionplanner.draw.service.MatchType
import com.graphite.competitionplanner.registration.domain.GetRegistrationsInCompetitionCategory
import com.graphite.competitionplanner.registration.interfaces.IRegistrationRepository
import com.graphite.competitionplanner.schedule.domain.interfaces.MatchDTO
import org.springframework.stereotype.Component
import kotlin.math.ceil

@Component
class CreateDraw(
    val getRegistrationsInCompetitionCategory: GetRegistrationsInCompetitionCategory,
    val findCompetitionCategory: FindCompetitionCategory,
    val createSeed: CreateSeed,
    val repository: IRegistrationRepository,
    val seedRepository: ISeedRepository
) {

    /**
     * Creates a draw for the given competition category
     */
    fun execute(competitionCategoryId: Int): CompetitionCategoryDrawDTO {
        val competitionCategory = findCompetitionCategory.byId(competitionCategoryId)

        // TODO: If draw already made, delete draw and associated matches and remake, seed, matches etc.

        val registrationRanks = repository.getRegistrationRank(competitionCategory)
        val registrationsWithSeeds = createSeed.execute(registrationRanks)
        seedRepository.setSeeds(registrationsWithSeeds)

        return competitionCategory.settings.let {
            when (it.drawType) {
                DrawType.POOL_ONLY,
                DrawType.POOL_AND_CUP
                -> CompetitionCategoryGroupsDrawDTO(competitionCategory.id, drawGroups(registrationsWithSeeds, it))
                DrawType.CUP_ONLY -> createPlayOffs(registrationsWithSeeds)
            }
        }
    }

    private fun drawGroups(registrations: List<RegistrationSeedDTO>, settings: GeneralSettingsSpec): List<Group> {
        val numberOfGroups = ceil((registrations.size.toDouble() / settings.playersPerGroup.toDouble())).toInt()

        val seededRegistrations = registrations.filter { it.seed != null }.sortedBy { it.seed!! }
        val nonSeededRegistrations = registrations.filter { it.seed == null }

        val groups = (1..numberOfGroups).toList().map {
            Group(it.asPoolName(), emptyList(), emptyList())
        }

        return addRoundRobin(groups, seededRegistrations + nonSeededRegistrations.shuffled())
    }

    /**
     * Adds a set of registrations in a round-robin fashion to a set of groups. The first registration is added to
     * the first group, then the second registration is added to the second group and so on. When all groups have
     * received their first round of registration, the process repeats.
     */
    private fun addRoundRobin(groups: List<Group>, registrations: List<RegistrationSeedDTO>): List<Group> {
        return if (registrations.isEmpty()) {
            groups
        } else {
            return if (registrations.size <= groups.size) {
                val first = registrations.first()
                val group = addRegistrationToGroup(groups.first(), first)
                val remaining = registrations.takeLast(registrations.size - 1)
                listOf(group) + addRoundRobin(groups.takeLast(groups.size - 1), remaining)
            } else {
                val round = addRoundRobin(groups, registrations.take(groups.size))
                addRoundRobin(round, registrations.takeLast(registrations.size - groups.size))
            }
        }
    }

    private fun addRegistrationToGroup(group: Group, registration: RegistrationSeedDTO): Group {
        return Group(group.name, group.registrationIds + listOf(registration.id), group.matches)
    }


    private fun createPlayOffs(registrations: List<RegistrationSeedDTO>): CompetitionCategoryPlayoffDrawDTO {
        return CompetitionCategoryPlayoffDrawDTO(1, emptyList())
    }

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

}

sealed class CompetitionCategoryDrawDTO(
    val competitionCategoryId: Int
)

class CompetitionCategoryPlayoffDrawDTO(
    competitionCategoryId: Int,
    val matches: List<Match>
) : CompetitionCategoryDrawDTO(competitionCategoryId)

class CompetitionCategoryGroupsDrawDTO(
    competitionCategoryId: Int,
    val groups: List<Group>
) : CompetitionCategoryDrawDTO(competitionCategoryId)

data class Group(
    val name: String,
    val registrationIds: List<Int>,
    val matches: List<MatchDTO>
)

data class Match(
    val type: MatchType,
//    val order: Int, Match order number. Keep it?
    val registrationOneId: Int,
    val registrationTwoId: Int
)