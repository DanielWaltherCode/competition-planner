package com.graphite.competitionplanner.schedule.domain

import com.graphite.competitionplanner.category.interfaces.ICategoryRepository
import com.graphite.competitionplanner.club.interfaces.IClubRepository
import com.graphite.competitionplanner.competition.domain.CreateCompetition
import com.graphite.competitionplanner.competition.domain.FindCompetitions
import com.graphite.competitionplanner.competition.interfaces.ICompetitionRepository
import com.graphite.competitionplanner.competitioncategory.interfaces.ICompetitionCategoryRepository
import com.graphite.competitionplanner.match.repository.MatchRepository
import com.graphite.competitionplanner.player.interfaces.IPlayerRepository
import com.graphite.competitionplanner.registration.interfaces.IRegistrationRepository
import com.graphite.competitionplanner.result.interfaces.IResultRepository
import com.graphite.competitionplanner.schedule.api.AvailableTablesAllDaysSpec
import com.graphite.competitionplanner.schedule.api.AvailableTablesSpec
import com.graphite.competitionplanner.schedule.service.AvailableTablesService
import com.graphite.competitionplanner.util.BaseRepositoryTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestGenerateTimeTable(
    @Autowired val createCompetition: CreateCompetition,
    @Autowired val timeTableSlotHandler: TimeTableSlotHandler,
    @Autowired val findCompetitions: FindCompetitions,
    @Autowired val availableTablesService: AvailableTablesService,
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

    @Test
    fun testGenerateTimeTables() {
        // Set up
        val club = newClub()
        val competition = createCompetition.execute(dataGenerator.newCompetitionSpec(organizingClubId = club.id))
        var tmpDate = competition.startDate
        while (tmpDate < competition.endDate) {
            availableTablesService.updateTablesAvailable(
                competition.id,
                AvailableTablesAllDaysSpec(listOf(AvailableTablesSpec(10, tmpDate)))
            )
            tmpDate = tmpDate.plusDays(1)
        }

        // Act
        timeTableSlotHandler.execute(competition.id)
    }
}