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
import com.graphite.competitionplanner.registration.domain.isReal
import com.graphite.competitionplanner.registration.interfaces.IRegistrationRepository
import com.graphite.competitionplanner.result.interfaces.IResultRepository
import com.graphite.competitionplanner.util.BaseRepositoryTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class TestAdvancingInPlayoff(
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
     * In this scenario we have 2 groups with 4 players each. Winner of groups will advance to playoff A and rest
     * will go to playoff B. This means we have 6 players in playoff B, where 2 of them will directly advance since
     * they go up against a BYE, leaving us with 2 other matches to be played in the quarter-finals.
     */
    @Test
    fun advancing_in_playoff_B() {
        // Setup
        val club = newClub()
        val competition = club.addCompetition()
        val competitionCategory = competition.addCompetitionCategory(
            DefaultCategory.MEN_1.name,
            drawType = DrawType.POOL_AND_CUP_WITH_B_PLAY_OFF,
            playersToPlayoff = 1) // 1 advance to playoff A and rest to B

        val suffix = listOf("A", "B", "C", "D", "E", "F", "G", "H")
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

        // Play all pool matches
        poolMatches.forEach {
            service.addFinalMatchResult(it.id, resultSpec)
        }

        // Act
        val drawAfterPoolMatches = getDraw.execute(competitionCategory.id)
        val quarterFinals = drawAfterPoolMatches.playOffB.first().matches.filter { m -> m.firstPlayer.first().id.isReal() && m.secondPlayer.first().id.isReal() }

        // Play quarter-finals in playoff B
        quarterFinals.forEach {
            service.addFinalMatchResult(it.id, resultSpec)
        }

        // Assertions
        val drawAfterQuarterFinals = getDraw.execute(competitionCategory.id)

        Assertions.assertEquals(1, drawAfterQuarterFinals.playOff.size,
            "Not the expected number of rounds in playoff A")
        Assertions.assertEquals(3, drawAfterQuarterFinals.playOffB.size,
            "Not the expected number of rounds in playoff B")

        Assertions.assertTrue(drawAfterQuarterFinals.playOffB.first().matches.all { it.winner.isNotEmpty() },
            "Expected all quarter finals to have winners")

        val winnersInQuarterFinals = drawAfterQuarterFinals.playOffB.first().matches.map { it.winner.first() }
        val semiFinals = drawAfterQuarterFinals.playOffB.first { it.round == Round.SEMI_FINAL }

        val firstSemifinal = semiFinals.matches.first()
        Assertions.assertEquals(winnersInQuarterFinals[0], firstSemifinal.firstPlayer.first(),
            "Winner from first quarter-final did not advance to first semi-final")
        Assertions.assertEquals(winnersInQuarterFinals[1], firstSemifinal.secondPlayer.first(),
            "Winner from second quarter-final did not advance to first semi-final")

        val secondSemifinal = semiFinals.matches.last()
        Assertions.assertEquals(winnersInQuarterFinals[2], secondSemifinal.firstPlayer.first(),
            "Winner from third quarter-final did not advance to second semi-final")
        Assertions.assertEquals(winnersInQuarterFinals[3], secondSemifinal.secondPlayer.first(),
            "Winner from fourth quarter-final did not advance to second semi-final")
    }
}