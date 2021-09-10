package com.graphite.competitionplanner.schedule

import com.graphite.competitionplanner.schedule.api.DailyStartAndEndSpec
import com.graphite.competitionplanner.competition.api.CompetitionSpec
import com.graphite.competitionplanner.schedule.repository.ScheduleRepository
import com.graphite.competitionplanner.competition.repository.CompetitionRepository
import com.graphite.competitionplanner.schedule.service.ScheduleService
import com.graphite.competitionplanner.competition.service.CompetitionService
import com.graphite.competitionplanner.util.Util
import org.junit.jupiter.api.AfterEach
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
    @Autowired val scheduleService: ScheduleService,
    @Autowired val scheduleRepository: ScheduleRepository,
    @Autowired val competitionService: CompetitionService,
    @Autowired val competitionRepository: CompetitionRepository
) {
    var competitionId = 0

    @BeforeEach
    fun addCompetition() {
        competitionId = competitionService.addCompetition(
            CompetitionSpec(
                location = "Lund",
                name = "Ein Testturnament",
                welcomeText = "Välkomna till Eurofinans",
                organizingClubId = util.getClubIdOrDefault("Lugi"),
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
        val dailyStartEnd = scheduleService.getDailyStartAndEndForWholeCompetition(competitionId)
        Assertions.assertNotNull(dailyStartEnd)
        Assertions.assertTrue(dailyStartEnd.dailyStartEndList.isNotEmpty())
    }

    @Test
    fun getDailyStartEndForDay() {
        val dailyStartEnd = scheduleService.getDailyStartAndEnd(competitionId, LocalDate.now())
        Assertions.assertNotNull(dailyStartEnd)
        Assertions.assertNotNull(dailyStartEnd.id)
        Assertions.assertNotNull(dailyStartEnd.startTime)
        Assertions.assertNotNull(dailyStartEnd.endTime)
        Assertions.assertNotNull(dailyStartEnd.day)
    }

    @Test
    fun updateDailyStartAndEnd() {
        // Update start and end times for first day of competition
        val dailyStartEnd = scheduleService.getDailyStartAndEnd(competitionId, LocalDate.now())
        val newStartTime = LocalTime.of(10, 0)
        val newEndTime = LocalTime.of(19, 0)

        val updateSpec = DailyStartAndEndSpec(
            LocalDate.now(),
            newStartTime,
            newEndTime
        )
        val updatedDailyStartAndEnd =
            scheduleService.updateDailyStartAndEnd(dailyStartEnd.id, competitionId, updateSpec)

        Assertions.assertNotNull(updatedDailyStartAndEnd)
        Assertions.assertEquals(newStartTime, updatedDailyStartAndEnd.startTime)
        Assertions.assertEquals(newEndTime, updatedDailyStartAndEnd.endTime)
        Assertions.assertEquals(LocalDate.now(), updatedDailyStartAndEnd.day)
    }
}