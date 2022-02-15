package com.graphite.competitionplanner.result.service

import com.graphite.competitionplanner.club.repository.ClubRepository
import com.graphite.competitionplanner.common.exception.GameValidationException
import com.graphite.competitionplanner.competitioncategory.domain.FindCompetitionCategory
import com.graphite.competitionplanner.competitioncategory.repository.CompetitionCategoryRepository
import com.graphite.competitionplanner.draw.domain.CreateDraw
import com.graphite.competitionplanner.match.service.MatchDTO
import com.graphite.competitionplanner.match.service.MatchService
import com.graphite.competitionplanner.player.domain.CreatePlayer
import com.graphite.competitionplanner.player.repository.PlayerRepository
import com.graphite.competitionplanner.registration.interfaces.RegistrationSinglesSpec
import com.graphite.competitionplanner.registration.repository.RegistrationRepository
import com.graphite.competitionplanner.registration.service.RegistrationService
import com.graphite.competitionplanner.result.api.GameSpec
import com.graphite.competitionplanner.result.api.ResultSpec
import com.graphite.competitionplanner.result.repository.ResultRepository
import com.graphite.competitionplanner.util.DataGenerator
import com.graphite.competitionplanner.util.TestUtil
import com.graphite.competitionplanner.util.Util
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import kotlin.random.Random

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
class TestResultService(
    @Autowired val testUtil: TestUtil,
    @Autowired val matchService: MatchService,
    @Autowired val registrationService: RegistrationService,
    @Autowired val registrationRepository: RegistrationRepository,
    @Autowired val resultService: ResultService,
    @Autowired val playerRepository: PlayerRepository,
    @Autowired val resultRepository: ResultRepository,
    @Autowired val util: Util,
    @Autowired val findCompetitionCategory: FindCompetitionCategory,
    @Autowired val competitionCategoryRepository: CompetitionCategoryRepository,
    @Autowired val createDraw: CreateDraw,
    @Autowired val clubRepository: ClubRepository,
    @Autowired val createPlayer: CreatePlayer
) {

    var competitionCategoryId = 0
    lateinit var result: ResultDTO
    lateinit var match: MatchDTO
    val dataGenerator = DataGenerator()

    @BeforeAll
    fun setUpClassData() {
        competitionCategoryId = testUtil.addCompetitionCategory("Herrar 3")
    }

    @BeforeEach
    fun setUpDataForEachTest() {
        // Update competition category so that it's groups of 3 instead with one proceeding
        val original = findCompetitionCategory.byId(competitionCategoryId)

        val updatedSettings = dataGenerator.newCompetitionCategoryUpdateSpec(
            settings = dataGenerator.newGeneralSettingsDTO(
                cost = original.settings.cost,
                drawType = original.settings.drawType,
                playersPerGroup = 3,
                playersToPlayOff = 2,
                poolDrawStrategy = original.settings.poolDrawStrategy
            ),
            gameSettings = dataGenerator.newGameSettingsDTO(
                original.gameSettings.numberOfSets,
                original.gameSettings.winScore,
                original.gameSettings.winMargin,
                original.gameSettings.differentNumberOfGamesFromRound,
                original.gameSettings.numberOfSetsFinal,
                original.gameSettings.winScoreFinal,
                original.gameSettings.winMarginFinal,
                original.gameSettings.tiebreakInFinalGame,
                original.gameSettings.winScoreTiebreak,
                original.gameSettings.winMarginTieBreak
            )
        )

        competitionCategoryRepository.update(original.id, updatedSettings)

        val club = clubRepository.store(dataGenerator.newClubSpec())
        val players = listOf(
            createPlayer.execute(dataGenerator.newPlayerSpec(clubId = club.id)),
            createPlayer.execute(dataGenerator.newPlayerSpec(clubId = club.id)),
            createPlayer.execute(dataGenerator.newPlayerSpec(clubId = club.id)),
            createPlayer.execute(dataGenerator.newPlayerSpec(clubId = club.id))
        )
        for (player in players) {
            registrationService.registerPlayerSingles(RegistrationSinglesSpec(player.id, competitionCategoryId))
        }
        createDraw.execute(competitionCategoryId)
    }

    @Test
    fun testAddResult() {
        addResult()
        Assertions.assertTrue(result.gameList.isNotEmpty())
        Assertions.assertNotNull(match.winner)
    }

    private fun addResult() {
        val matches = matchService.getMatchesInCategory(competitionCategoryId)

        // Add result for first match
        val gameList = mutableListOf<GameSpec>()
        gameList.add(GameSpec(1, 11, 9))
        gameList.add(GameSpec(2, 11, 9))
        gameList.add(GameSpec(3, 11, 9))
        val resultSpec = ResultSpec(gameList)
        this.result = resultService.addResult(matches[0].id, resultSpec)
        this.match = matchService.getMatch(matches[0].id)

    }

    @Test
    fun testAddFaultyResults() {
        val matches = matchService.getMatchesInCategory(competitionCategoryId)

        val gameList = mutableListOf<GameSpec>()
        gameList.add(GameSpec(1, 11, 9))
        gameList.add(GameSpec(2, 11, 9))
        gameList.add(GameSpec(3, 10, 9))
        val resultSpec = ResultSpec(gameList)

        Assertions.assertThrows(GameValidationException::class.java) {
            resultService.addResult(matches[1].id, resultSpec)
        }

        // Assert winner of match has not been set
        val match = matchService.getMatch(matches[1].id)
        Assertions.assertEquals(0, match.winner.size)
    }

//    @Test
//    fun testGetResult() {
//        addResult()
//        val result = resultService.getResult(match.id)
//        Assertions.assertNotNull(result.gameList)
//        Assertions.assertEquals(3, result.gameList.size)
//
//    }

    @Test
    fun testUpdateFullResults() {
        addResult()
        val originalSize = resultRepository.countResults()

        // Update
        val gameList = mutableListOf<GameSpec>()
        gameList.add(GameSpec(1, 11, 9))
        gameList.add(GameSpec(2, 11, 6))
        gameList.add(GameSpec(3, 11, 6))
        val resultSpec = ResultSpec(gameList)
        val updatedResult = resultService.addFinalMatchResult(match.id, resultSpec)

        //Assertions
        val newSize = resultRepository.countResults()
        Assertions.assertEquals(originalSize, newSize)
        Assertions.assertEquals(6, updatedResult.gameList.get(1).secondRegistrationResult)
    }

    @Test
    fun testUpdateResultsInOneGame() {
        addResult()
        val result = resultService.getResult(match.id)
        val originalSize = resultRepository.countResults()

        // Update
        val gameSpec = GameSpec(2, 11, 6)
        val updatedResult = resultService.updateGameResult(match.id, result.gameList.get(1).gameId, gameSpec)

        //Assertions
        val newSize = resultRepository.countResults()
        Assertions.assertEquals(originalSize, newSize)
        Assertions.assertEquals(6, updatedResult.gameList.get(1).secondRegistrationResult)
    }

}