package com.graphite.competitionplanner.schedule.service

import com.graphite.competitionplanner.category.interfaces.ICategoryRepository
import com.graphite.competitionplanner.club.interfaces.IClubRepository
import com.graphite.competitionplanner.competition.domain.CreateCompetition
import com.graphite.competitionplanner.competition.interfaces.CompetitionSpec
import com.graphite.competitionplanner.competition.interfaces.ICompetitionRepository
import com.graphite.competitionplanner.competition.interfaces.LocationSpec
import com.graphite.competitionplanner.competitioncategory.interfaces.ICompetitionCategoryRepository
import com.graphite.competitionplanner.match.repository.MatchRepository
import com.graphite.competitionplanner.player.interfaces.IPlayerRepository
import com.graphite.competitionplanner.registration.interfaces.IRegistrationRepository
import com.graphite.competitionplanner.result.interfaces.IResultRepository
import com.graphite.competitionplanner.schedule.api.DailyStartAndEndSpec
import com.graphite.competitionplanner.util.BaseRepositoryTest
import com.graphite.competitionplanner.util.Util
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDate
import java.time.LocalTime

@SpringBootTest
class TestScheduleDailyTimes(
    @Autowired val util: Util,
    @Autowired val dailyStartEndService: DailyStartEndService,
    @Autowired val createCompetition: CreateCompetition,
    @Autowired clubRepository: IClubRepository,
    @Autowired competitionRepository: ICompetitionRepository,
    @Autowired competitionCategoryRepository: ICompetitionCategoryRepository,
    @Autowired categoryRepository: ICategoryRepository,
    @Autowired playerRepository: IPlayerRepository,
    @Autowired registrationRepository: IRegistrationRepository,
    @Autowired matchRepository: MatchRepository,
    @Autowired resultRepository: IResultRepository,
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

    var competitionId = 0

    @BeforeEach
    fun addCompetition() {
        val club = newClub()
        competitionId = createCompetition.execute(
                CompetitionSpec(
                        location = LocationSpec("Lund"),
                        name = "Ein Testturnament",
                        welcomeText = "Välkomna till Eurofinans",
                        organizingClubId = club.id,
                        competitionLevel = "A",
                        startDate = LocalDate.now(),
                        endDate = LocalDate.now().plusDays(10)
                )
        ).id
    }

    @Test
    fun getDailyStartEndForCompetition() {
        // Act
        val dailyStartEnd = dailyStartEndService.getDailyStartAndEndForWholeCompetition(competitionId)

        // Assert
        Assertions.assertNotNull(dailyStartEnd)
        Assertions.assertTrue(dailyStartEnd.dailyStartEndList.isNotEmpty())

        val sorted = dailyStartEnd.dailyStartEndList.sortedBy { it.day }
        Assertions.assertEquals(sorted, dailyStartEnd.dailyStartEndList, "StartEndList was not sorted")

        val sortedAvailableDays = dailyStartEnd.availableDays.sorted()
        Assertions.assertEquals(sortedAvailableDays, dailyStartEnd.availableDays, "Available days was not sorted")
    }

    @Test
    fun getDailyStartEndForDay() {
        val dailyStartEnd = dailyStartEndService.getDailyStartAndEnd(competitionId, LocalDate.now())
        Assertions.assertNotNull(dailyStartEnd)
        Assertions.assertNotNull(dailyStartEnd.id)
        Assertions.assertNotNull(dailyStartEnd.startTime)
        Assertions.assertNotNull(dailyStartEnd.endTime)
        Assertions.assertNotNull(dailyStartEnd.day)
    }

    @Test
    fun updateDailyStartAndEnd() {
        // Update start and end times for first day of competition
        val newStartTime = LocalTime.of(10, 0)
        val newEndTime = LocalTime.of(19, 0)

        val updateSpec = DailyStartAndEndSpec(
                LocalDate.now(),
                newStartTime,
                newEndTime
        )
        dailyStartEndService.updateDailyStartAndEnd(competitionId, updateSpec)

        val updatedDailyStartAndEnd = dailyStartEndService.getDailyStartAndEnd(competitionId, LocalDate.now())

        Assertions.assertNotNull(updatedDailyStartAndEnd)
        Assertions.assertEquals(newStartTime, updatedDailyStartAndEnd.startTime)
        Assertions.assertEquals(newEndTime, updatedDailyStartAndEnd.endTime)
        Assertions.assertEquals(LocalDate.now(), updatedDailyStartAndEnd.day)
    }
}