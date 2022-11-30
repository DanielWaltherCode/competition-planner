package com.graphite.competitionplanner.result.service

import com.graphite.competitionplanner.category.domain.DefaultCategory
import com.graphite.competitionplanner.club.repository.ClubRepository
import com.graphite.competitionplanner.competitioncategory.domain.FindCompetitionCategory
import com.graphite.competitionplanner.competitioncategory.repository.CompetitionCategoryRepository
import com.graphite.competitionplanner.draw.domain.CreateDraw
import com.graphite.competitionplanner.draw.repository.CompetitionDrawRepository
import com.graphite.competitionplanner.match.domain.Match
import com.graphite.competitionplanner.match.repository.MatchRepository
import com.graphite.competitionplanner.match.service.MatchService
import com.graphite.competitionplanner.player.domain.CreatePlayer
import com.graphite.competitionplanner.player.repository.PlayerRepository
import com.graphite.competitionplanner.registration.interfaces.RegistrationSinglesSpec
import com.graphite.competitionplanner.registration.repository.RegistrationRepository
import com.graphite.competitionplanner.registration.service.RegistrationService
import com.graphite.competitionplanner.result.api.GameSpec
import com.graphite.competitionplanner.result.api.ResultSpec
import com.graphite.competitionplanner.result.domain.AddPartialResult
import com.graphite.competitionplanner.result.repository.ResultRepository
import com.graphite.competitionplanner.util.DataGenerator
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
    @Autowired val createPlayer: CreatePlayer,
    @Autowired val addPartialResult: AddPartialResult,
    @Autowired val matchRepository: MatchRepository,
    @Autowired val competitionDrawRepository: CompetitionDrawRepository
) {

    var competitionCategoryId = 0
    lateinit var result: ResultDTO
    lateinit var match: Match
    val dataGenerator = DataGenerator()

    @BeforeAll
    fun setUpClassData() {
        competitionCategoryId = testUtil.addCompetitionCategory(DefaultCategory.MEN_3)
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
                original.gameSettings.numberOfSetsInPlayoff,
                original.gameSettings.winScoreInPlayoff,
                original.gameSettings.winMarginInPlayoff,
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

    @AfterEach
    fun cleanUp() {
        val matches = matchService.getMatchesInCategory(competitionCategoryId)
        for (match in matches) {
            resultRepository.deleteResults(match.id)
        }
        // Remove pool draw
        competitionDrawRepository.deleteGroupsInCategory(competitionCategoryId)

        // Delete pools
        competitionDrawRepository.deletePools(competitionCategoryId)
    }

    private fun addPartialResult() {
        val matches = matchService.getMatchesInCategory(competitionCategoryId)

        // Add result for first match
        val gameList = mutableListOf<GameSpec>()
        gameList.add(GameSpec(1, 11, 9))
        gameList.add(GameSpec(2, 11, 9))
        gameList.add(GameSpec(3, 11, 9))

        this.match = matchRepository.getMatch2(matches[0].id)
        this.result = addPartialResult.execute(match.id, ResultSpec(gameList))
    }

    @Test
    fun testAddFinalMatchResults() {
        // Setup
        addPartialResult()
        val originalSize = matchRepository.getMatch2(match.id).result.size

        // Act
        val gameList = mutableListOf<GameSpec>()
        gameList.add(GameSpec(1, 11, 9))
        gameList.add(GameSpec(2, 11, 6))
        gameList.add(GameSpec(3, 11, 6))
        val resultSpec = ResultSpec(gameList)
        val updatedResult = resultService.addFinalMatchResult(match.id, resultSpec)

        //Assertions
        val newSize = matchRepository.getMatch2(match.id).result.size
        Assertions.assertEquals(originalSize, newSize, "Size of result set changed")
        Assertions.assertEquals(6, updatedResult.gameList[1].secondRegistrationResult)
    }

}