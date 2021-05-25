package com.graphite.competitionplanner.result

import com.graphite.competitionplanner.api.GameSpec
import com.graphite.competitionplanner.api.ResultSpec
import com.graphite.competitionplanner.api.competition.CategoryMetadataSpec
import com.graphite.competitionplanner.api.competition.RegistrationSinglesSpec
import com.graphite.competitionplanner.repositories.PlayerRepository
import com.graphite.competitionplanner.repositories.RegistrationRepository
import com.graphite.competitionplanner.repositories.ResultRepository
import com.graphite.competitionplanner.repositories.competition.CompetitionDrawRepository
import com.graphite.competitionplanner.service.*
import com.graphite.competitionplanner.service.competition.CompetitionCategoryService
import com.graphite.competitionplanner.service.competition.DrawService
import com.graphite.competitionplanner.util.TestUtil
import com.graphite.competitionplanner.util.Util
import com.graphite.competitionplanner.util.exception.GameValidationException
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
class TestResultService(
    @Autowired val testUtil: TestUtil,
    @Autowired val matchService: MatchService,
    @Autowired val playerService: PlayerService,
    @Autowired val competitionCategoryService: CompetitionCategoryService,
    @Autowired val registrationService: RegistrationService,
    @Autowired val competitionDrawRepository: CompetitionDrawRepository,
    @Autowired val registrationRepository: RegistrationRepository,
    @Autowired val categoryService: CategoryService,
    @Autowired val resultService: ResultService,
    @Autowired val playerRepository: PlayerRepository,
    @Autowired val drawService: DrawService,
    @Autowired val resultRepository: ResultRepository,
    @Autowired val util: Util
) {

    var competitionCategoryId = 0
    lateinit var result: ResultDTO
    lateinit var match: MatchDTO

    @BeforeAll
    fun setUpClassData() {
        competitionCategoryId = testUtil.addCompetitionCategory("Herrar 5")
    }

    @BeforeEach
    fun setUpDataForEachTest() {
        // Update competition category so that it's groups of 3 instead with one proceeding
        val categoryMetadata = categoryService.getCategoryMetadata(competitionCategoryId)
        val categoryMetadataSpec = CategoryMetadataSpec(
            cost = categoryMetadata.cost,
            drawTypeId = categoryMetadata.drawType.id,
            nrPlayersPerGroup = 3,
            nrPlayersToPlayoff = 1,
            poolDrawStrategyId = categoryMetadata.poolDrawStrategyId
        )

        categoryService.updateCategoryMetadata(competitionCategoryId, categoryMetadata.id, categoryMetadataSpec)

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

        // Add too few sets
        var gameList = mutableListOf<GameSpec>()
        gameList.add(GameSpec(1, 11, 9))
        gameList.add(GameSpec(2, 11, 9))
        var resultSpec = ResultSpec(gameList)
        Assertions.assertThrows(GameValidationException::class.java) {
            resultService.addResult(matches[1].id, resultSpec)
        }

        gameList = mutableListOf<GameSpec>()
        gameList.add(GameSpec(1, 11, 9))
        gameList.add(GameSpec(2, 11, 9))
        gameList.add(GameSpec(3, 10, 9))
        resultSpec = ResultSpec(gameList)

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
        val updatedResult = resultService.updateFullMatchResult(match.id, resultSpec)

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