package com.graphite.competitionplanner.result.service

import com.graphite.competitionplanner.category.interfaces.ICategoryRepository
import com.graphite.competitionplanner.club.interfaces.IClubRepository
import com.graphite.competitionplanner.competition.interfaces.ICompetitionRepository
import com.graphite.competitionplanner.competitioncategory.interfaces.CompetitionCategoryDTO
import com.graphite.competitionplanner.competitioncategory.interfaces.ICompetitionCategoryRepository
import com.graphite.competitionplanner.draw.domain.CreateDraw
import com.graphite.competitionplanner.match.domain.Match
import com.graphite.competitionplanner.match.repository.MatchRepository
import com.graphite.competitionplanner.match.service.MatchService
import com.graphite.competitionplanner.player.domain.CreatePlayer
import com.graphite.competitionplanner.player.interfaces.IPlayerRepository
import com.graphite.competitionplanner.registration.interfaces.IRegistrationRepository
import com.graphite.competitionplanner.registration.interfaces.RegistrationSinglesSpec
import com.graphite.competitionplanner.registration.service.RegistrationService
import com.graphite.competitionplanner.result.api.GameSpec
import com.graphite.competitionplanner.result.api.ResultSpec
import com.graphite.competitionplanner.result.domain.AddPartialResult
import com.graphite.competitionplanner.result.interfaces.IResultRepository
import com.graphite.competitionplanner.util.*
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
class TestResultService(
    @Autowired val matchService: MatchService,
    @Autowired val registrationService: RegistrationService,
    @Autowired val resultService: ResultService,
    @Autowired val util: Util,
    @Autowired val createDraw: CreateDraw,
    @Autowired val createPlayer: CreatePlayer,
    @Autowired val addPartialResult: AddPartialResult,
    @Autowired clubRepository: IClubRepository,
    @Autowired competitionRepository: ICompetitionRepository,
    @Autowired competitionCategoryRepository: ICompetitionCategoryRepository,
    @Autowired categoryRepository: ICategoryRepository,
    @Autowired playerRepository: IPlayerRepository,
    @Autowired registrationRepository: IRegistrationRepository,
    @Autowired matchRepository: MatchRepository,
    @Autowired resultRepository: IResultRepository,
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

    lateinit var result: ResultDTO
    lateinit var match: Match
    lateinit var competitionCategory: CompetitionCategoryDTO

    @BeforeEach
    fun setUpDataForEachTest() {
        val club = newClub()
        val competition = club.addCompetition()
        competitionCategory = competition.addCompetitionCategory()

        // Update competition category so that it's groups of 3 instead with one proceeding
        val updatedSettings = dataGenerator.newCompetitionCategoryUpdateSpec(
            settings = dataGenerator.newGeneralSettingsDTO(
                cost = competitionCategory.settings.cost,
                drawType = competitionCategory.settings.drawType,
                playersPerGroup = 3,
                playersToPlayOff = 2,
                poolDrawStrategy = competitionCategory.settings.poolDrawStrategy
            ),
            gameSettings = dataGenerator.newGameSettingsDTO(
                competitionCategory.gameSettings.numberOfSets,
                competitionCategory.gameSettings.winScore,
                competitionCategory.gameSettings.winMargin,
                competitionCategory.gameSettings.differentNumberOfGamesFromRound,
                competitionCategory.gameSettings.numberOfSetsInPlayoff,
                competitionCategory.gameSettings.winScoreInPlayoff,
                competitionCategory.gameSettings.winMarginInPlayoff,
                competitionCategory.gameSettings.tiebreakInFinalGame,
                competitionCategory.gameSettings.winScoreTiebreak,
                competitionCategory.gameSettings.winMarginTieBreak
            )
        )

        competitionCategoryRepository.update(competitionCategory.id, updatedSettings)

        val players = listOf(
            createPlayer.execute(dataGenerator.newPlayerSpec(clubId = club.id)),
            createPlayer.execute(dataGenerator.newPlayerSpec(clubId = club.id)),
            createPlayer.execute(dataGenerator.newPlayerSpec(clubId = club.id)),
            createPlayer.execute(dataGenerator.newPlayerSpec(clubId = club.id))
        )
        for (player in players) {
            registrationService.registerPlayerSingles(RegistrationSinglesSpec(player.id, competitionCategory.id))
        }
        createDraw.execute(competitionCategory.id)
    }

    private fun addPartialResult() {
        val matches = matchService.getMatchesInCategory(competitionCategory.id)

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