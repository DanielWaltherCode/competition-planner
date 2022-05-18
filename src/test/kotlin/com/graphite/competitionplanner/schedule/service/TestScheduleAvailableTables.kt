package com.graphite.competitionplanner.schedule.service

import com.graphite.competitionplanner.competition.domain.CreateCompetition
import com.graphite.competitionplanner.competition.interfaces.CompetitionSpec
import com.graphite.competitionplanner.competition.interfaces.LocationSpec
import com.graphite.competitionplanner.competition.repository.CompetitionRepository
import com.graphite.competitionplanner.schedule.api.AvailableTablesSpec
import com.graphite.competitionplanner.util.Util
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDate

@SpringBootTest
class TestScheduleAvailableTables(
    @Autowired val util: Util,
    @Autowired val availableTablesService: AvailableTablesService,
    @Autowired val competitionRepository: CompetitionRepository,
    @Autowired val createCompetition: CreateCompetition
) {
    var competitionId = 0
    lateinit var registeredTables: AvailableTablesDTO

    @BeforeEach
    fun addCompetition() {
        competitionId = createCompetition.execute(
            CompetitionSpec(
                location = LocationSpec("Lund"),
                name = "Ein Testturnament",
                welcomeText = "Välkomna till Eurofinans",
                organizingClubId = util.getClubIdOrDefault("Lugi"),
                "A",
                startDate = LocalDate.now(),
                endDate = LocalDate.now().plusDays(10)
            )
        ).id
        val spec = AvailableTablesSpec(
            15,
            LocalDate.now()
        )
        registeredTables = availableTablesService.updateTablesAvailable(competitionId, spec)
    }

    @AfterEach
    fun deleteCompetition() {
        competitionRepository.deleteCompetition(competitionId)
    }

    @Test
    fun getTablesAvailableForFullDay() {
        val availableTables = availableTablesService.getTablesAvailable(competitionId)
        Assertions.assertNotNull(availableTables)
        Assertions.assertTrue(availableTables.isNotEmpty())
        Assertions.assertEquals(availableTables[0].nrTables, 15)
    }

    @Test
    fun updateTablesAvailable() {
        val tableToUpdate = availableTablesService.getTablesAvailableByDay(competitionId, LocalDate.now())
        val newNrTables = 18
        val spec = AvailableTablesSpec(
            newNrTables,
            LocalDate.now(),
        )
        val updatedAvailableTable = availableTablesService.updateTablesAvailable(competitionId, spec)
        Assertions.assertEquals(newNrTables, updatedAvailableTable.nrTables)
        Assertions.assertEquals(tableToUpdate.id, updatedAvailableTable.id)
        Assertions.assertEquals(tableToUpdate.day, updatedAvailableTable.day)
    }
}
