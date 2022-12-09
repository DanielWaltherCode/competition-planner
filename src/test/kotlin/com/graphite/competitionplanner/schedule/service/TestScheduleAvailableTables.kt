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
import com.graphite.competitionplanner.schedule.api.AvailableTablesAllDaysSpec
import com.graphite.competitionplanner.schedule.api.AvailableTablesSpec
import com.graphite.competitionplanner.util.BaseRepositoryTest
import com.graphite.competitionplanner.util.Util
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
    @Autowired val createCompetition: CreateCompetition,
    @Autowired competitionCategoryRepository: ICompetitionCategoryRepository,
    @Autowired clubRepository: IClubRepository,
    @Autowired competitionRepository: ICompetitionRepository,
    @Autowired categoryRepository: ICategoryRepository,
    @Autowired playerRepository: IPlayerRepository,
    @Autowired registrationRepository: IRegistrationRepository,
    @Autowired matchRepository: MatchRepository,
    @Autowired resultRepository: IResultRepository
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
                "A",
                startDate = LocalDate.now(),
                endDate = LocalDate.now().plusDays(10)
            )
        ).id
        val spec = AvailableTablesSpec(
            15,
            LocalDate.now()
        )
        availableTablesService.updateTablesAvailable(competitionId, AvailableTablesAllDaysSpec(listOf(spec)))
    }

    @Test
    fun getTablesAvailableForFullDay() {
        val availableTables = availableTablesService.getTablesAvailable(competitionId)
        Assertions.assertNotNull(availableTables)
        Assertions.assertTrue(availableTables.isNotEmpty())
        Assertions.assertEquals(availableTables[0].nrTables, 15)
    }
}
