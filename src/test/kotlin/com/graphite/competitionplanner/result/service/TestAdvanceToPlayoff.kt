package com.graphite.competitionplanner.result.service

import com.graphite.competitionplanner.category.domain.DefaultCategory
import com.graphite.competitionplanner.category.interfaces.ICategoryRepository
import com.graphite.competitionplanner.club.interfaces.IClubRepository
import com.graphite.competitionplanner.competition.interfaces.ICompetitionRepository
import com.graphite.competitionplanner.competitioncategory.interfaces.DrawType
import com.graphite.competitionplanner.competitioncategory.interfaces.ICompetitionCategoryRepository
import com.graphite.competitionplanner.draw.domain.CreateDraw
import com.graphite.competitionplanner.draw.domain.GetDraw
import com.graphite.competitionplanner.draw.interfaces.Round
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
class TestAdvanceToPlayoff(
    @Autowired val createDraw: CreateDraw,
    @Autowired val service: ResultService,
    @Autowired val getDraw: GetDraw,
    @Autowired clubRepository: IClubRepository,
    @Autowired competitionRepository: ICompetitionRepository,
    @Autowired competitionCategoryRepository: ICompetitionCategoryRepository,
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

    /**
     * In this scenario we have 10 players, pools size of 4, which means we will have 3 pools. One pool will have
     * size 4, and two pools size 3. We also have 2 players advancing from each group i.e. 6 players in total. This
     * makes it, so we have to BYEs in first round i.e. the quarter-final. One BYE against A1 and one BYE against B1
     */
    @Test
    fun whenFirstRoundIsAgainstBye() {
        // Setup
        val club = newClub()
        val competition = club.addCompetition()
        val competitionCategory = competition.addCompetitionCategory(
            DefaultCategory.MEN_1.name,
            drawType = DrawType.POOL_AND_CUP)

        val suffix = listOf("A", "B", "C", "D", "E", "F", "G", "H", "J", "H")
        val players = suffix.map {
            club.addPlayer("Player$it")
        }

        players.map { competitionCategory.registerPlayer(it) }

        createDraw.execute(competitionCategory.id)

        val draw = getDraw.execute(competitionCategory.id)

        val poolMatches = draw.groups.flatMap { it.matches }

        val resultSpec = dataGenerator.newResultSpec(
            games = listOf(
                dataGenerator.newGameSpec(gameNumber = 1),
                dataGenerator.newGameSpec(gameNumber = 2),
                dataGenerator.newGameSpec(gameNumber = 3))
        )

        // Act
        poolMatches.forEach {
            service.addFinalMatchResult(it.id, resultSpec)
        }

        // Assert
        val playerIds = players.map { it.id }
        val drawAfterPoolCompleted = getDraw.execute(competitionCategory.id)
        val quarterFinal = drawAfterPoolCompleted.playOff.first { it.round == Round.QUARTER_FINAL }

        val a1VsBye = quarterFinal.matches.first { it.matchOrderNumber == 1 }
        val b1VsBye = quarterFinal.matches.first { it.matchOrderNumber == 4 }

        Assertions.assertTrue(a1VsBye.winner.isNotEmpty(),
            "Expected to find a winner in a game against a BYE")
        Assertions.assertTrue(playerIds.contains(a1VsBye.winner.first().id),
            "The BYE advanced instead of the actual player")

        Assertions.assertTrue(b1VsBye.winner.isNotEmpty(),
            "Expected to find a winner in a game against a BYE")
        Assertions.assertTrue(playerIds.contains(b1VsBye.winner.first().id),
            "The BYE advanced instead of the actual player")

        val semiFinal = drawAfterPoolCompleted.playOff.first { it.round == Round.SEMI_FINAL }
        val a1VsPlaceholder = semiFinal.matches.first { it.matchOrderNumber == 1 }
        val b1VsPlaceholder = semiFinal.matches.first { it.matchOrderNumber == 2 }

        Assertions.assertEquals(a1VsBye.winner.first().id, a1VsPlaceholder.firstPlayer.first().id,
        "A1 did not immediately advance to next round")

        Assertions.assertEquals(b1VsBye.winner.first().id, b1VsPlaceholder.secondPlayer.first().id,
            "B1 did not immediately advance to next round")
    }
}