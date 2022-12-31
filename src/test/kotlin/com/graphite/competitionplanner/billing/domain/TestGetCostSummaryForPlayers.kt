package com.graphite.competitionplanner.billing.domain

import com.graphite.competitionplanner.category.interfaces.ICategoryRepository
import com.graphite.competitionplanner.club.interfaces.IClubRepository
import com.graphite.competitionplanner.competition.interfaces.ICompetitionRepository
import com.graphite.competitionplanner.competitioncategory.interfaces.ICompetitionCategoryRepository
import com.graphite.competitionplanner.match.repository.MatchRepository
import com.graphite.competitionplanner.player.interfaces.IPlayerRepository
import com.graphite.competitionplanner.registration.interfaces.IRegistrationRepository
import com.graphite.competitionplanner.result.interfaces.IResultRepository
import com.graphite.competitionplanner.util.BaseRepositoryTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestGetCostSummaryForPlayers(
    @Autowired val getCostSummaryForPlayers: GetCostSummaryForPlayers,
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
    fun getCostSummaryForPlayers() {
        // Setup
        val hostClub = newClub()
        val competition = hostClub.addCompetition()
        val competitionCategory = competition.addCompetitionCategory()

        val otherClub = newClub()
        val players = listOf("a", "b", "c", "d", "e", "f").map { suffix ->  otherClub.addPlayer("Player$suffix") }
        players.forEach { player -> competitionCategory.registerPlayer(player) }

        // Act
        val costSummary = getCostSummaryForPlayers.execute(competition.id, otherClub.id)

        // Assert
        Assertions.assertTrue(costSummary.costSummaryList.isNotEmpty())
        Assertions.assertTrue(costSummary.totalPrice > 0)
    }
}