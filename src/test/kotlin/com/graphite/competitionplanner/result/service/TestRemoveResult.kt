package com.graphite.competitionplanner.result.service

import com.graphite.competitionplanner.category.domain.DefaultCategory
import com.graphite.competitionplanner.category.interfaces.ICategoryRepository
import com.graphite.competitionplanner.club.interfaces.IClubRepository
import com.graphite.competitionplanner.common.exception.BadRequestException
import com.graphite.competitionplanner.common.exception.BadRequestType
import com.graphite.competitionplanner.competition.interfaces.ICompetitionRepository
import com.graphite.competitionplanner.competitioncategory.interfaces.DrawType
import com.graphite.competitionplanner.competitioncategory.interfaces.ICompetitionCategoryRepository
import com.graphite.competitionplanner.draw.domain.CreateDraw
import com.graphite.competitionplanner.draw.domain.GetDraw
import com.graphite.competitionplanner.draw.interfaces.CompetitionCategoryDrawDTO
import com.graphite.competitionplanner.draw.interfaces.PlayoffRoundDTO
import com.graphite.competitionplanner.draw.interfaces.Round
import com.graphite.competitionplanner.match.repository.MatchRepository
import com.graphite.competitionplanner.player.interfaces.IPlayerRepository
import com.graphite.competitionplanner.registration.domain.Registration
import com.graphite.competitionplanner.registration.domain.asInt
import com.graphite.competitionplanner.registration.interfaces.IRegistrationRepository
import com.graphite.competitionplanner.result.api.GameSpec
import com.graphite.competitionplanner.result.interfaces.IResultRepository
import com.graphite.competitionplanner.util.BaseRepositoryTest
import com.graphite.competitionplanner.util.SetupTestData
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestRemoveResult(
    @Autowired val setupTestData: SetupTestData,
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

    @BeforeEach
    fun setupByeAndPlaceholderRegistrations() {
        setupTestData.trySetupOverigtClub()
        setupTestData.trySetupByeAndPlaceHolder()
    }

    private val inputTestData = listOf(
        DrawType.POOL_AND_CUP_WITH_B_PLAY_OFF,
        DrawType.POOL_AND_CUP)

    /**
     * This test works by playing all the matches except one in the pool stage. Then we record the state of the
     * draw at that moment. Followed by playing last game and immediately removing that game's result. Then
     * we expect the state of the draw to be equal to what we initially recorded.
     */
    @TestFactory
    fun whenPlayoffHasJustStarted() = inputTestData
        .map { drawType ->
            DynamicTest.dynamicTest("When draw type is ${drawType.name}") {
                // Setup
                val club = newClub()
                val competition = club.addCompetition()
                val competitionCategory = competition.addCompetitionCategory(
                    DefaultCategory.MEN_1.name,
                    drawType = drawType
                )

                val suffix = listOf("A", "B", "C", "D")
                val players = suffix.map {
                    club.addPlayer("Player$it")
                }

                players.forEach { competitionCategory.registerPlayer(it) }

                val draw = createDraw.execute(competitionCategory.id)

                val poolMatches = draw.groups.flatMap { it.matches }

                val gameResults = listOf(
                    GameSpec(1, 11, 0),
                    GameSpec(2, 11, 0),
                    GameSpec(3, 11, 0)
                )

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

                val groupStandingBefore = drawJustBeforeLastMatch.groups.first().groupStandingList
                val groupStandingAfter = afterRemovingResult.groups.first().groupStandingList
                Assertions.assertEquals(
                    groupStandingBefore[0], groupStandingAfter[0],
                    "Position 1 in the group is not the same as before we removed the result"
                )
                Assertions.assertEquals(
                    groupStandingBefore[1], groupStandingAfter[1],
                    "Position 2 in the group is not the same as before we removed the result"
                )
                Assertions.assertEquals(
                    2,
                    groupStandingBefore[2].matchesPlayed,
                    "Not the correct number of matches played"
                )
                Assertions.assertEquals(
                    2,
                    groupStandingBefore[3].matchesPlayed,
                    "Not the correct number of matches played"
                )

                val subGroupStandingBefore = drawJustBeforeLastMatch.groups.first().subGroupList
                val subGroupStandingAfter = afterRemovingResult.groups.first().subGroupList
                Assertions.assertEquals(
                    subGroupStandingBefore.size, subGroupStandingAfter.size,
                    "Not expected size of sub group standing"
                )
                Assertions.assertEquals(
                    subGroupStandingBefore.flatMap { it.groupStandingList.flatMap { sub -> sub.player.map { p -> p.firstName } } }
                        .sorted(),
                    subGroupStandingAfter.flatMap { it.groupStandingList.flatMap { sub -> sub.player.map { p -> p.firstName } } }
                        .sorted(),
                    "Not the same players in the sub group"
                )

                Assertions.assertEquals(
                    drawJustBeforeLastMatch.playOff, afterRemovingResult.playOff,
                    "The playoff was not rolled back properly"
                )
                Assertions.assertEquals(
                    drawJustBeforeLastMatch.playOffB, afterRemovingResult.playOffB,
                    "The playoff B was not rolled back properly"
                )
            }
    }

    @Test
    fun reverting_playoff_A_and_B_matches(){
        // Setup
        val club = newClub()
        val competition = club.addCompetition()
        val competitionCategory = competition.addCompetitionCategory(
            DefaultCategory.MEN_1.name,
            drawType = DrawType.POOL_AND_CUP_WITH_B_PLAY_OFF
        )

        val suffix = listOf("A", "B", "C", "D")
        val players = suffix.map {
            club.addPlayer("Player$it")
        }

        players.forEach { competitionCategory.registerPlayer(it) }

        val draw = createDraw.execute(competitionCategory.id)

        playAllPoolGames(draw)

        // Play finals in each playoff
        val gameResults = listOf(
            GameSpec(1, 11, 0),
            GameSpec(2, 11, 0),
            GameSpec(3, 11, 0)
        )
        val finalA = draw.playOff.first().matches.first()
        service.addFinalMatchResult(finalA.id, dataGenerator.newResultSpec(games = gameResults))

        val finalB = draw.playOffB.first().matches.first()
        service.addFinalMatchResult(finalB.id, dataGenerator.newResultSpec(games = gameResults))

        // Act
        service.deleteResults(finalA.id)
        service.deleteResults(finalB.id)

        // Assert
        val drawAfterDeletedResult = getDraw.execute(competitionCategory.id)

        val finalMatchA = drawAfterDeletedResult.playOff.first().matches.first()
        Assertions.assertTrue(finalMatchA.firstPlayer.first().id > 0, "First player was reset")
        Assertions.assertTrue(finalMatchA.secondPlayer.first().id > 0, "Second player was reset")
        Assertions.assertTrue(finalMatchA.winner.isEmpty(), "Winner was not reset")
        Assertions.assertTrue(finalMatchA.result.gameList.isEmpty(), "Result was not reset")

        val finalMatchB = drawAfterDeletedResult.playOff.first().matches.first()
        Assertions.assertTrue(finalMatchB.firstPlayer.first().id > 0, "First player was reset")
        Assertions.assertTrue(finalMatchB.secondPlayer.first().id > 0, "Second player was reset")
        Assertions.assertTrue(finalMatchB.winner.isEmpty(), "Winner was not reset")
        Assertions.assertTrue(finalMatchB.result.gameList.isEmpty(), "Result was not reset")
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
        Assertions.assertTrue(finalMatch.firstPlayer.first().id == Registration.Placeholder().asInt() ,
            "First player was not reset to be a Placeholder")
        Assertions.assertTrue(finalMatch.secondPlayer.first().id == Registration.Placeholder().asInt(),
            "Second player was not reset to be a Placeholder")
    }

    @Test
    fun should_not_be_able_to_remove_pool_results_if_playoff_has_started() {
        // Setup
        val club = newClub()
        val competition = club.addCompetition()
        val competitionCategory = competition.addCompetitionCategory(
            DefaultCategory.MEN_1.name,
            drawType = DrawType.POOL_AND_CUP_WITH_B_PLAY_OFF)

        val suffix = listOf("A", "B", "C", "D", "E", "F", "G", "H")
        val players = suffix.map {
            club.addPlayer("Player$it")
        }

        players.forEach { competitionCategory.registerPlayer(it) }

        val draw = createDraw.execute(competitionCategory.id)

        playAllPoolGames(draw)
        playAllSemifinals(draw.playOff)

        // Act
        val poolMatch = draw.groups.first().matches.first()
        val exception = Assertions.assertThrows(BadRequestException::class.java) {
            service.deleteResults(poolMatch.id)
        }
        Assertions.assertEquals(BadRequestType.RESULT_CANNOT_DELETE, exception.exceptionType)
        Assertions.assertEquals("Cannot delete result from pool match when play-off has started. Remove all play-off results first.", exception.errorMessage)
    }

    @Test
    fun whenRemovingPreviousRoundResultInPlayoffB() {
        // Setup
        val club = newClub()
        val competition = club.addCompetition()
        val competitionCategory = competition.addCompetitionCategory(
            DefaultCategory.MEN_1.name,
            drawType = DrawType.POOL_AND_CUP_WITH_B_PLAY_OFF)

        val suffix = listOf("A", "B", "C", "D", "E", "F", "G", "H")
        val players = suffix.map {
            club.addPlayer("Player$it")
        }

        players.forEach { competitionCategory.registerPlayer(it) }

        val draw = createDraw.execute(competitionCategory.id)

        // Play all matches up until finals
        playAllPoolGames(draw)
        playAllSemifinals(draw.playOff)
        playAllSemifinals(draw.playOffB)

        // Act
        removeResultsInSemifinals(draw.playOff)
        removeResultsInSemifinals(draw.playOffB)

        // Assert
        val drawAfterDeletedResult = getDraw.execute(competitionCategory.id)
        assertAllSemifinalsWasRevertedProperly(drawAfterDeletedResult.playOff)
        assertAllSemifinalsWasRevertedProperly(drawAfterDeletedResult.playOffB)

        assertFinalWasRevertedProperly(drawAfterDeletedResult.playOff)
        assertFinalWasRevertedProperly(drawAfterDeletedResult.playOffB)
    }

    private fun assertFinalWasRevertedProperly(playOff: List<PlayoffRoundDTO>) {
        val finalMatch = playOff.first { it.round == Round.FINAL }.matches.first()
        Assertions.assertTrue(finalMatch.firstPlayer.first().id == Registration.Placeholder().asInt() ,
            "First player was not reset to be a Placeholder")
        Assertions.assertTrue(finalMatch.secondPlayer.first().id == Registration.Placeholder().asInt(),
            "Second player was not reset to be a Placeholder")
    }

    private fun assertAllSemifinalsWasRevertedProperly(playOff: List<PlayoffRoundDTO>) {
        playOff.filter { it.round == Round.SEMI_FINAL }.forEach { round ->
            round.matches.forEach { semifinal ->
                Assertions.assertTrue(semifinal.firstPlayer.first().id > 0, "First player was reset")
                Assertions.assertTrue(semifinal.secondPlayer.first().id > 0, "Second player was reset")
                Assertions.assertTrue(semifinal.winner.isEmpty(), "Winner was not reset")
                Assertions.assertTrue(semifinal.result.gameList.isEmpty(), "Result was not reset")
            }
        }
    }

    private fun removeResultsInSemifinals(playOff: List<PlayoffRoundDTO>) {
        playOff.filter { it.round == Round.SEMI_FINAL }.forEach { round ->
            round.matches.forEach { match ->
                service.deleteResults(match.id)
            }
        }
    }

    private fun playAllSemifinals(playOff: List<PlayoffRoundDTO>) {
        val gameResults = listOf(
            GameSpec(1, 11, 0),
            GameSpec(2, 11, 0),
            GameSpec(3, 11, 0))

        playOff.filter { it.round == Round.SEMI_FINAL }.forEach { round ->
            round.matches.forEach { match ->
                service.addFinalMatchResult(match.id, dataGenerator.newResultSpec(games = gameResults))
            }
        }
    }

    private fun playAllPoolGames(draw: CompetitionCategoryDrawDTO) {
        val poolMatches = draw.groups.flatMap { it.matches }

        val gameResults = listOf(
            GameSpec(1, 11, 0),
            GameSpec(2, 11, 0),
            GameSpec(3, 11, 0)
        )

        for (match in poolMatches) {
            service.addFinalMatchResult(match.id, dataGenerator.newResultSpec(games = gameResults))
        }
    }
}