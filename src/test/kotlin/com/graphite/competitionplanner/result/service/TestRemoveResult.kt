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
import com.graphite.competitionplanner.registration.domain.Registration
import com.graphite.competitionplanner.registration.domain.asInt
import com.graphite.competitionplanner.registration.interfaces.IRegistrationRepository
import com.graphite.competitionplanner.result.api.GameSpec
import com.graphite.competitionplanner.result.interfaces.IResultRepository
import com.graphite.competitionplanner.util.BaseRepositoryTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestRemoveResult(
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
     * This test works by playing all the matches except one in the pool stage. Then we record the state of the
     * draw at that moment. Followed by playing last game and immediately removing that game's result. Then
     * we expect the state of the draw to be equal to what we initially recorded.
     */
    @Test
    fun whenPlayoffHasJustStarted() {
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

        players.forEach { competitionCategory.registerPlayer(it) }

        val draw = createDraw.execute(competitionCategory.id)

        val poolMatches = draw.groups.flatMap { it.matches }

        val gameResults = listOf(
            GameSpec(1, 11, 0),
            GameSpec(2, 11, 0),
            GameSpec(3, 11, 0))

        val lastMatch = poolMatches.last()
        val rest = poolMatches.dropLast(1)

        for (match in rest) {
            service.addFinalMatchResult(match.id, dataGenerator.newResultSpec(games = gameResults))
        }

        // Record state
        val drawJustBeforeLastMatch = getDraw.execute(competitionCategory.id)

        // Act
        service.addFinalMatchResult(lastMatch.id, dataGenerator.newResultSpec(games = gameResults))
        service.deleteResults(lastMatch.id)

        // Assert
        val afterRemovingResult = getDraw.execute(competitionCategory.id)

        Assertions.assertEquals(drawJustBeforeLastMatch.groups.first().name, afterRemovingResult.groups.first().name)
        Assertions.assertEquals(drawJustBeforeLastMatch.groups.first().matches, afterRemovingResult.groups.first().matches)
        Assertions.assertEquals(drawJustBeforeLastMatch.groups.first().players, afterRemovingResult.groups.first().players)

        // TODO: If we figure out how to get stable group standing and subgroup list we can uncomment here.
//        Assertions.assertEquals(drawJustBeforeLastMatch.groups.first().groupStandingList, afterRemovingResult.groups.first().groupStandingList)
//        Assertions.assertEquals(drawJustBeforeLastMatch.groups.first().subGroupList, afterRemovingResult.groups.first().subGroupList)
//        Assertions.assertEquals(drawJustBeforeLastMatch.groups, afterRemovingResult.groups,
//            "The group stage was not rolled back properly")
        Assertions.assertEquals(drawJustBeforeLastMatch.playOff, afterRemovingResult.playOff,
            "The playoff was not rolled back properly"
        )
    }

    @Test
    fun whenRemovingFinalPlayoffMatchResult() {
        // Setup
        val club = newClub()
        val competition = club.addCompetition()
        val competitionCategory = competition.addCompetitionCategory(
            DefaultCategory.MEN_1.name,
            drawType = DrawType.CUP_ONLY)

        val suffix = listOf("A", "B", "C", "D")
        val players = suffix.map {
            club.addPlayer("Player$it")
        }

        players.forEach { competitionCategory.registerPlayer(it) }

        val draw = createDraw.execute(competitionCategory.id)

        // Play all matches in playoff
        val gameResults = listOf(
            GameSpec(1, 11, 0),
            GameSpec(2, 11, 0),
            GameSpec(3, 11, 0))

        draw.playOff.forEach { round ->
            round.matches.forEach { match ->
                service.addFinalMatchResult(match.id, dataGenerator.newResultSpec(games = gameResults))
            }
        }

        // Act
        val finalRound = draw.playOff.first { it.round == Round.FINAL }
        service.deleteResults(finalRound.matches.first().id)

        // Assert
        val drawAfterDeletedResult = getDraw.execute(competitionCategory.id)
        val finalMatch = drawAfterDeletedResult.playOff.first { it.round == Round.FINAL }.matches.first()
        Assertions.assertTrue(finalMatch.firstPlayer.first().id > 0, "First player was reset")
        Assertions.assertTrue(finalMatch.secondPlayer.first().id > 0, "Second player was reset")
        Assertions.assertTrue(finalMatch.winner.isEmpty(), "Winner was not reset")
        Assertions.assertTrue(finalMatch.result.gameList.isEmpty(), "Result was not reset")
    }

    @Test
    fun whenRemovingPreviousRoundResult() {
        // Setup
        val club = newClub()
        val competition = club.addCompetition()
        val competitionCategory = competition.addCompetitionCategory(
            DefaultCategory.MEN_1.name,
            drawType = DrawType.CUP_ONLY)

        val suffix = listOf("A", "B", "C", "D")
        val players = suffix.map {
            club.addPlayer("Player$it")
        }

        players.forEach { competitionCategory.registerPlayer(it) }

        val draw = createDraw.execute(competitionCategory.id)

        // Play all semi-finals
        val gameResults = listOf(
            GameSpec(1, 11, 0),
            GameSpec(2, 11, 0),
            GameSpec(3, 11, 0))

        draw.playOff.filter { it.round == Round.SEMI_FINAL }.forEach { round ->
            round.matches.forEach { match ->
                service.addFinalMatchResult(match.id, dataGenerator.newResultSpec(games = gameResults))
            }
        }

        // Act
        draw.playOff.filter { it.round == Round.SEMI_FINAL }.forEach { round ->
            round.matches.forEach { match ->
                service.deleteResults(match.id)
            }
        }

        // Assert
        val drawAfterDeletedResult = getDraw.execute(competitionCategory.id)
        drawAfterDeletedResult.playOff.filter { it.round == Round.SEMI_FINAL }.forEach { round ->
            round.matches.forEach { semifinal ->
                Assertions.assertTrue(semifinal.firstPlayer.first().id > 0, "First player was reset")
                Assertions.assertTrue(semifinal.secondPlayer.first().id > 0, "Second player was reset")
                Assertions.assertTrue(semifinal.winner.isEmpty(), "Winner was not reset")
                Assertions.assertTrue(semifinal.result.gameList.isEmpty(), "Result was not reset")
            }
        }

        val finalMatch = drawAfterDeletedResult.playOff.first { it.round == Round.FINAL }.matches.first()
        Assertions.assertTrue(finalMatch.firstPlayer.first().id == Registration.Placeholder().asInt() , "First player was not reset to be a Placeholder")
        Assertions.assertTrue(finalMatch.secondPlayer.first().id == Registration.Placeholder().asInt(), "Second player was not reset to be a Placeholder")
    }
}