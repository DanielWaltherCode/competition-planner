package com.graphite.competitionplanner.schedule;

import com.graphite.competitionplanner.api.AvailableTablesFullDaySpec
import com.graphite.competitionplanner.api.AvailableTablesSpec
import com.graphite.competitionplanner.api.competition.CompetitionSpec
import com.graphite.competitionplanner.repositories.ScheduleRepository;
import com.graphite.competitionplanner.repositories.competition.CompetitionRepository;
import com.graphite.competitionplanner.service.AvailableTablesDTO
import com.graphite.competitionplanner.service.ScheduleService;
import com.graphite.competitionplanner.service.competition.CompetitionService;
import com.graphite.competitionplanner.util.Util;
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.time.LocalDate

@SpringBootTest
class TestScheduleAvailableTables(
    @Autowired val util: Util,
    @Autowired val scheduleService: ScheduleService,
    @Autowired val scheduleRepository: ScheduleRepository,
    @Autowired val competitionService: CompetitionService,
    @Autowired val competitionRepository: CompetitionRepository
) {
    var competitionId = 0
    var registeredTables = listOf<AvailableTablesDTO>()

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
        val spec = AvailableTablesFullDaySpec(
            15,
            LocalDate.now()
        )
        registeredTables = scheduleService.registerTablesAvailableFullDay(competitionId, spec)
    }

    @AfterEach
    fun deleteCompetition() {
        competitionRepository.deleteCompetition(competitionId)
    }

    @Test
    fun addTablesAvailableForFullDay() {
        Assertions.assertNotNull(registeredTables)
        Assertions.assertTrue(registeredTables.isNotEmpty())
        Assertions.assertEquals(registeredTables[0].nrTables, 15)
    }

    @Test
    fun getTablesAvailableForFullDay() {
        val availableTables = scheduleService.getTablesAvailableByDay(competitionId, LocalDate.now())
        Assertions.assertNotNull(availableTables)
        Assertions.assertTrue(availableTables.isNotEmpty())
        Assertions.assertEquals(availableTables[0].nrTables, 15)
    }

    @Test
    fun updateTablesAvailable() {
        val availableTables = scheduleService.getTablesAvailableByDay(competitionId, LocalDate.now())
        val tableToUpdate = availableTables[0]
        val newNrTables = 18
        val spec = AvailableTablesSpec(
            newNrTables,
            tableToUpdate.day,
            tableToUpdate.hour
        )
        val updatedAvailableTable = scheduleService.updateTablesAvailable(tableToUpdate.id, competitionId, spec)
        Assertions.assertEquals(newNrTables, updatedAvailableTable.nrTables)
        Assertions.assertEquals(tableToUpdate.id, updatedAvailableTable.id)
        Assertions.assertEquals(tableToUpdate.day, updatedAvailableTable.day)
        Assertions.assertEquals(tableToUpdate.hour, updatedAvailableTable.hour)
    }
}
