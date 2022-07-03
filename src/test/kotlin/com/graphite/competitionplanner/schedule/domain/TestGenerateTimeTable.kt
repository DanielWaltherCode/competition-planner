package com.graphite.competitionplanner.schedule.domain

import com.graphite.competitionplanner.competition.domain.FindCompetitions
import com.graphite.competitionplanner.schedule.api.AvailableTablesAllDaysSpec
import com.graphite.competitionplanner.schedule.api.AvailableTablesSpec
import com.graphite.competitionplanner.schedule.service.AvailableTablesService
import com.graphite.competitionplanner.util.TestUtil
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

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
            availableTablesService.updateTablesAvailable(competition.id, AvailableTablesAllDaysSpec(listOf(AvailableTablesSpec(10, tmpDate))))
            tmpDate =  tmpDate.plusDays(1)
        }

        // Act
        timeTableSlotHandler.execute(competition.id)
    }
}