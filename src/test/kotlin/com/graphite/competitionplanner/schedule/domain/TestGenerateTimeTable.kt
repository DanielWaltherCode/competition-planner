package com.graphite.competitionplanner.schedule.domain

import com.graphite.competitionplanner.competition.domain.FindCompetitions
import com.graphite.competitionplanner.schedule.api.AvailableTablesSpec
import com.graphite.competitionplanner.schedule.api.AvailableTablesWholeCompetitionSpec
import com.graphite.competitionplanner.schedule.interfaces.IScheduleRepository
import com.graphite.competitionplanner.schedule.service.AvailableTablesService
import com.graphite.competitionplanner.util.DataGenerator
import com.graphite.competitionplanner.util.TestUtil
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDate
import java.util.concurrent.TimeUnit
import kotlin.time.toDuration

@SpringBootTest
class TestGenerateTimeTable(
        @Autowired val timeTableSlotHandler: TimeTableSlotHandler,
        @Autowired val findCompetitions: FindCompetitions,
        @Autowired val testUtil: TestUtil,
        @Autowired val availableTablesService: AvailableTablesService
) {

    @Test
    fun testGenerateTimeTables() {
        // Set up
        val clubId = testUtil.getClubIdOrDefault("Lugi")
        val competition = findCompetitions.thatBelongTo(clubId)[0]
        var tmpDate = competition.startDate
        while (tmpDate < competition.endDate) {
            availableTablesService.updateTablesAvailable(competition.id, AvailableTablesSpec(10, tmpDate))
            tmpDate =  tmpDate.plusDays(1)
        }

        // Act
        timeTableSlotHandler.init(competition.id)
    }
}