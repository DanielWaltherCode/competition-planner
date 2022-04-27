package com.graphite.competitionplanner.schedule.service

import com.graphite.competitionplanner.competition.domain.CreateCompetition
import com.graphite.competitionplanner.competition.interfaces.CompetitionSpec
import com.graphite.competitionplanner.competition.interfaces.LocationSpec
import com.graphite.competitionplanner.competition.repository.CompetitionRepository
import com.graphite.competitionplanner.schedule.api.DailyStartAndEndSpec
import com.graphite.competitionplanner.util.Util
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDate
import java.time.LocalTime

@SpringBootTest
class TestScheduleDailyTimes(
    @Autowired val util: Util,
    @Autowired val dailyStartEndService: DailyStartEndService,
    @Autowired val competitionRepository: CompetitionRepository,
    @Autowired val createCompetition: CreateCompetition
) {
    var competitionId = 0

    @BeforeEach
    fun addCompetition() {
        competitionId = createCompetition.execute(
            CompetitionSpec(
                location = LocationSpec("Lund"),
                name = "Ein Testturnament",
                welcomeText = "Välkomna till Eurofinans",
                organizingClubId = util.getClubIdOrDefault("Lugi"),
                competitionLevel = "A",
                startDate = LocalDate.now(),
                endDate = LocalDate.now().plusDays(10)
            )
        ).id
    }

    @AfterEach
    fun deleteCompetition() {
        competitionRepository.deleteCompetition(competitionId)
    }

    @Test
    fun getDailyStartEndForCompetition() {
        // This should be registered already when competition is added
        val dailyStartEnd = dailyStartEndService.getDailyStartAndEndForWholeCompetition(competitionId)
        Assertions.assertNotNull(dailyStartEnd)
        Assertions.assertTrue(dailyStartEnd.dailyStartEndList.isNotEmpty())
    }

    @Test
    fun getDailyStartEndForDay() {
        val dailyStartEnd = dailyStartEndService.getDailyStartAndEnd(competitionId, LocalDate.now())
            ?: fail("Expected to find daily start end")
        Assertions.assertNotNull(dailyStartEnd)
        Assertions.assertNotNull(dailyStartEnd.id)
        Assertions.assertNotNull(dailyStartEnd.startTime)
        Assertions.assertNotNull(dailyStartEnd.endTime)
        Assertions.assertNotNull(dailyStartEnd.day)
    }

    @Test
    fun updateDailyStartAndEnd() {
        // Update start and end times for first day of competition
        val dailyStartEnd = dailyStartEndService.getDailyStartAndEnd(competitionId, LocalDate.now())
            ?: fail("Expected to find daily start end")
        val newStartTime = LocalTime.of(10, 0)
        val newEndTime = LocalTime.of(19, 0)

        val updateSpec = DailyStartAndEndSpec(
            LocalDate.now(),
            newStartTime,
            newEndTime
        )
        val updatedDailyStartAndEnd =
            dailyStartEndService.updateDailyStartAndEnd(dailyStartEnd.id, competitionId, updateSpec)
                ?: fail("Expected to find daily start end")

        Assertions.assertNotNull(updatedDailyStartAndEnd)
        Assertions.assertEquals(newStartTime, updatedDailyStartAndEnd.startTime)
        Assertions.assertEquals(newEndTime, updatedDailyStartAndEnd.endTime)
        Assertions.assertEquals(LocalDate.now(), updatedDailyStartAndEnd.day)
    }
}