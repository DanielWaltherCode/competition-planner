package com.graphite.competitionplanner.schedule.repository

import com.graphite.competitionplanner.category.interfaces.ICategoryRepository
import com.graphite.competitionplanner.club.interfaces.IClubRepository
import com.graphite.competitionplanner.competition.interfaces.ICompetitionRepository
import com.graphite.competitionplanner.competitioncategory.interfaces.CompetitionCategoryDTO
import com.graphite.competitionplanner.competitioncategory.interfaces.ICompetitionCategoryRepository
import com.graphite.competitionplanner.match.domain.Match
import com.graphite.competitionplanner.match.repository.MatchRepository
import com.graphite.competitionplanner.player.interfaces.IPlayerRepository
import com.graphite.competitionplanner.registration.interfaces.IRegistrationRepository
import com.graphite.competitionplanner.registration.interfaces.RegistrationSinglesDTO
import com.graphite.competitionplanner.result.interfaces.IResultRepository
import com.graphite.competitionplanner.schedule.domain.TimeInterval
import com.graphite.competitionplanner.schedule.interfaces.IScheduleRepository
import com.graphite.competitionplanner.util.BaseRepositoryTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDate
import java.time.LocalDateTime

@SpringBootTest
class TestPreSchedule @Autowired constructor(
    val scheduleRepository: IScheduleRepository,
    clubRepository: IClubRepository,
    competitionRepository: ICompetitionRepository,
    competitionCategoryRepository: ICompetitionCategoryRepository,
    categoryRepository: ICategoryRepository,
    playerRepository: IPlayerRepository,
    registrationRepository: IRegistrationRepository,
    matchRepository: MatchRepository,
    resultRepository: IResultRepository
) : BaseRepositoryTest(
    clubRepository,
    competitionRepository,
    competitionCategoryRepository,
    categoryRepository,
    playerRepository,
    registrationRepository,
    matchRepository,
    resultRepository
) {

    @Test
    fun testStoringPreSchedule() {
        // Setup
        val club = newClub()
        val competition = club.addCompetition()
        val competitionCategory = competition.addCompetitionCategory()
        val preSchedule = dataGenerator.newPreScheduleSpec(competitionCategoryId = competitionCategory.id)

        // Act & Assert
        scheduleRepository.storePreSchedule(competition.id, preSchedule)
        Assertions.assertDoesNotThrow {
            scheduleRepository.storePreSchedule(competition.id, preSchedule)
        }
    }

    @Test
    fun testUpdatingPreSchedule() {
        // Setup
        val club = newClub()
        val competition = club.addCompetition()
        val competitionCategory1 = competition.addCompetitionCategory("Herrar 1")
        val competitionCategory2 = competition.addCompetitionCategory("Herrar 2")
        val preSchedule1 = dataGenerator.newPreScheduleSpec(competitionCategoryId = competitionCategory1.id)
        val preSchedule2 = dataGenerator.newPreScheduleSpec(competitionCategoryId = competitionCategory2.id)
        scheduleRepository.storePreSchedule(competition.id, preSchedule1)
        scheduleRepository.storePreSchedule(competition.id, preSchedule2)

        val estimatedTime = LocalDateTime.of(2022, 1, 3, 9, 0, 0)
        val success = false

        // Act
        scheduleRepository.update(listOf(competitionCategory1.id, competitionCategory2.id), estimatedTime, success)

        // Assert
        val preSchedule = scheduleRepository.getPreSchedule(competition.id)

        Assertions.assertTrue(
            preSchedule.all { it.success == success && it.estimatedEndTime == estimatedTime },
            "At least one of the competition categories was not updated: $preSchedule"
        )
    }

    @Test
    fun testGetPreScheduledMatches() {
        // Setup
        val club = newClub()
        val competition = club.addCompetition()
        val menCompetitionCategory = competition.addCompetitionCategory("Herrar 1")
        val womenCompetitionCategory = competition.addCompetitionCategory("Damer 1")

        val playerHalvard = club.addPlayer("Halvard")
        val playerThomas = club.addPlayer("Thomas")
        val playerErika = club.addPlayer("Erika")
        val playerMatilda = club.addPlayer("Matilda")

        val halvardRegistration = menCompetitionCategory.registerPlayer(playerHalvard)
        val thomasRegistration = menCompetitionCategory.registerPlayer(playerThomas)
        val erikaRegistration = womenCompetitionCategory.registerPlayer(playerErika)
        val matildaRegistration = womenCompetitionCategory.registerPlayer(playerMatilda)

        val today = LocalDate.now()
        scheduleRepository.storePreSchedule(
            competition.id,
            dataGenerator.newPreScheduleSpec(
                competitionCategoryId = menCompetitionCategory.id,
                playDate = today,
                timeInterval = TimeInterval.EVENING
            )
        )
        scheduleRepository.storePreSchedule(
            competition.id,
            dataGenerator.newPreScheduleSpec(
                competitionCategoryId = womenCompetitionCategory.id,
                playDate = today,
                timeInterval = TimeInterval.MORNING
            )
        )

        menCompetitionCategory.addMatches(3, halvardRegistration, thomasRegistration)
        womenCompetitionCategory.addMatches(4, erikaRegistration, matildaRegistration)

        // Act
        val morningMatches = scheduleRepository.getPreScheduledMatches(competition.id, today, TimeInterval.MORNING)
        val eveningMatches = scheduleRepository.getPreScheduledMatches(competition.id, today, TimeInterval.EVENING)

        // Assert
        Assertions.assertEquals(4, morningMatches.size, "Not the correct number of matches returned.")
        Assertions.assertEquals(3, eveningMatches.size, "Not the correct number of matches returned.")
    }

    fun CompetitionCategoryDTO.addMatches(
        numberOfMatches: Int,
        first: RegistrationSinglesDTO,
        second: RegistrationSinglesDTO
    ): List<Match> {
        return (1..numberOfMatches).map {
            matchRepository.store(
                dataGenerator.newMatchSpec(
                    competitionCategoryId = this.id,
                    firstRegistrationId = first.id,
                    secondRegistrationId = second.id
                )
            )
        }
    }
}