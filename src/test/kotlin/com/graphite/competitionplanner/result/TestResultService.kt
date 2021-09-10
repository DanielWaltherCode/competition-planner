package com.graphite.competitionplanner.result

import com.graphite.competitionplanner.DataGenerator
import com.graphite.competitionplanner.common.exception.GameValidationException
import com.graphite.competitionplanner.competitioncategory.service.CompetitionCategoryService
import com.graphite.competitionplanner.draw.repository.CompetitionDrawRepository
import com.graphite.competitionplanner.draw.service.DrawService
import com.graphite.competitionplanner.match.service.MatchDTO
import com.graphite.competitionplanner.match.service.MatchService
import com.graphite.competitionplanner.player.repository.PlayerRepository
import com.graphite.competitionplanner.registration.api.RegistrationSinglesSpec
import com.graphite.competitionplanner.registration.repository.RegistrationRepository
import com.graphite.competitionplanner.registration.service.RegistrationService
import com.graphite.competitionplanner.result.api.GameSpec
import com.graphite.competitionplanner.result.api.ResultSpec
import com.graphite.competitionplanner.result.repository.ResultRepository
import com.graphite.competitionplanner.result.service.ResultDTO
import com.graphite.competitionplanner.result.service.ResultService
import com.graphite.competitionplanner.util.TestUtil
import com.graphite.competitionplanner.util.Util
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
class TestResultService(
    @Autowired val testUtil: TestUtil,
    @Autowired val matchService: MatchService,
    @Autowired val competitionCategoryService: CompetitionCategoryService,
    @Autowired val registrationService: RegistrationService,
    @Autowired val competitionDrawRepository: CompetitionDrawRepository,
    @Autowired val registrationRepository: RegistrationRepository,
    @Autowired val resultService: ResultService,
    @Autowired val playerRepository: PlayerRepository,
    @Autowired val drawService: DrawService,
    @Autowired val resultRepository: ResultRepository,
    @Autowired val util: Util
) {

    var competitionCategoryId = 0
    lateinit var result: ResultDTO
    lateinit var match: MatchDTO
    val dataGenerator = DataGenerator()

    @BeforeAll
    fun setUpClassData() {
        competitionCategoryId = testUtil.addCompetitionCategory("Herrar 5")
    }

    @BeforeEach
    fun setUpDataForEachTest() {
        // Update competition category so that it's groups of 3 instead with one proceeding
        val original = competitionCategoryService.getByCompetitionCategoryId(competitionCategoryId)

        val updatedSettings = dataGenerator.newCompetitionCategoryUpdateDTO(
            original.id,
            settings = dataGenerator.newGeneralSettingsDTO(
                cost = original.settings.cost,
                drawType = original.settings.drawType,
                playersPerGroup = 3,
                playersToPlayOff = 1,
                poolDrawStrategy = original.settings.poolDrawStrategy
            ),
            gameSettings = original.gameSettings
        )

        competitionCategoryService.updateCompetitionCategory(updatedSettings)

        // Add players
        val allPlayers = playerRepository.getAll()

        // Get 22 players
        val players = allPlayers.subList(0, 3)
        for (player in players) {
            registrationService.registerPlayerSingles(RegistrationSinglesSpec(player.id, competitionCategoryId))
        }
        drawService.createDraw(competitionCategoryId)
    }

    @AfterEach
    fun deleteDataForEachTest() {
        // Remove matches
        matchService.deleteMatchesInCategory(competitionCategoryId)

        // Remove pool draw
        competitionDrawRepository.deleteGroupsInCategory(competitionCategoryId)

        // Remove registrations and delete category
        val registrationIds = registrationRepository.getRegistrationIdsInCategory(competitionCategoryId)
        for (id in registrationIds) {
            registrationService.unregister(id)
        }
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

    @Test
    fun testGetResult() {
        addResult()
        val result = resultService.getResult(match.id)
        Assertions.assertNotNull(result.gameList)
        Assertions.assertEquals(3, result.gameList.size)

    }

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