package com.graphite.competitionplanner.draw.domain

import com.graphite.competitionplanner.competition.repository.CompetitionRepository
import com.graphite.competitionplanner.competitioncategory.domain.GetCompetitionCategories
import com.graphite.competitionplanner.draw.repository.CompetitionDrawRepository
import com.graphite.competitionplanner.draw.service.DrawService
import com.graphite.competitionplanner.match.repository.MatchRepository
import com.graphite.competitionplanner.player.interfaces.PlayerDTO
import com.graphite.competitionplanner.player.repository.PlayerRepository
import com.graphite.competitionplanner.registration.interfaces.RegistrationSinglesSpec
import com.graphite.competitionplanner.registration.repository.RegistrationRepository
import com.graphite.competitionplanner.registration.service.RegistrationService
import com.graphite.competitionplanner.result.api.GameSpec
import com.graphite.competitionplanner.result.api.ResultSpec
import com.graphite.competitionplanner.result.service.ResultService
import com.graphite.competitionplanner.tables.records.MatchRecord
import com.graphite.competitionplanner.util.TestUtil
import com.graphite.competitionplanner.util.Util
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import kotlin.random.Random

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
class TestCalculateGroupStanding(
    @Autowired val getDraw: GetDraw,
    @Autowired val competitionRepository: CompetitionRepository,
    @Autowired val getCompetitionCategories: GetCompetitionCategories,
    @Autowired val util: Util,
    @Autowired val testUtil: TestUtil,
    @Autowired val playerRepository: PlayerRepository,
    @Autowired val registrationService: RegistrationService,
    @Autowired val drawService: DrawService,
    @Autowired val matchRepository: MatchRepository,
    @Autowired val competitionDrawRepository: CompetitionDrawRepository,
    @Autowired val registrationRepository: RegistrationRepository,
    @Autowired val resultService: ResultService,
    @Autowired val calculateGroupStanding: CalculateGroupStanding,
    @Autowired val createDraw: CreateDraw
) {

    var competitionCategoryId = 0
    var uniquePlayerRegistrations: MutableSet<Int> = mutableSetOf()

    @BeforeAll
    fun setUpClassData() {
        competitionCategoryId = testUtil.addCompetitionCategory("Herrar 6")
    }

    @BeforeEach
    fun setUpTestData() {
        // Add players
        val players = playerRepository.getAll()

        for (player in players) {
            registrationService.registerPlayerSingles(RegistrationSinglesSpec(player.id, competitionCategoryId))
        }
        createDraw.execute(competitionCategoryId)

        val matches = matchRepository.getMatchesInCategory(competitionCategoryId)
        val matchesInGroupA = matches.filter { it.groupOrRound == "A" }
        uniquePlayerRegistrations = mutableSetOf()
        for (match in matchesInGroupA) {
            uniquePlayerRegistrations.add(match.firstRegistrationId)
            uniquePlayerRegistrations.add(match.secondRegistrationId)
        }
        uniquePlayerRegistrations.sorted()
        Assertions.assertEquals(4, uniquePlayerRegistrations.size)
    }

    @AfterEach
    fun deleteDataForEachTest() {
        // Remove matches
        matchRepository.deleteMatchesForCategory(competitionCategoryId)

        // Remove pool draw
        competitionDrawRepository.deleteGroupsInCategory(competitionCategoryId)

        // Remove registrations and delete category
        val registrationIds = registrationRepository.getRegistrationIdsInCategory(competitionCategoryId)
        for (id in registrationIds) {
            registrationService.unregister(id)
        }

    }

    @Test
    fun testGeneralGroupStanding() {
        val lugiId = util.getClubIdOrDefault("Lugi")
        val lugiCompetitionId = competitionRepository.findCompetitionsThatBelongTo(lugiId)[0].id
        val competitionCategories = getCompetitionCategories.execute(lugiCompetitionId)
        val herrar2 = competitionCategories[1]
        val draw = getDraw.execute(herrar2.id)

        for (group in draw.groups) {
            val totalMatches = group.matches.size
            var matchParticipations = 0
            var matchesWon = 0
            var matchesLost = 0
            var gamesWon = 0
            var gamesLost = 0
            var pointsWon = 0
            var pointsLost = 0
            for (result in group.groupStandingList) {
                matchParticipations += result.matchesPlayed
                matchesWon += result.matchesWonLost.won
                matchesLost += result.matchesWonLost.lost

                gamesWon += result.gamesWonLost.won
                gamesLost += result.gamesWonLost.lost

                pointsWon += result.pointsWonLost.won
                pointsLost += result.pointsWonLost.lost
            }
            // There are 2 "participations" in each match
            Assertions.assertEquals(totalMatches * 2, matchParticipations)
            Assertions.assertEquals(totalMatches, matchesWon)
            Assertions.assertEquals(totalMatches, matchesLost)
            Assertions.assertEquals(gamesWon, gamesLost)
            Assertions.assertEquals(pointsWon, pointsLost)

        }
    }

    /*
    * The easiest case - player 1 wins 3, player 2 wins 2, player 3 wins 1, player 4 wins 0
     */
    @Test
    fun testDistinctGroupScores() {
        // Setup
        winAgainstPlayersX(
            uniquePlayerRegistrations.elementAt(0),
            mutableListOf(
                uniquePlayerRegistrations.elementAt(1),
                uniquePlayerRegistrations.elementAt(2),
                uniquePlayerRegistrations.elementAt(3),
            )
        )

        winAgainstPlayersX(
            uniquePlayerRegistrations.elementAt(1),
            mutableListOf(
                uniquePlayerRegistrations.elementAt(2),
                uniquePlayerRegistrations.elementAt(3),
            )
        )

        winAgainstPlayersX(
            uniquePlayerRegistrations.elementAt(2),
            mutableListOf(
                uniquePlayerRegistrations.elementAt(3),
            )
        )

        val playerDTOList: MutableList<List<PlayerDTO>> = mutableListOf()
        for (registration in uniquePlayerRegistrations) {
            playerDTOList.add(registrationRepository.getPlayersFrom(registration))
        }

        // Act
        val groupStanding = calculateGroupStanding.execute(competitionCategoryId, "A")

        // Assert
        var counter = 1
        for (player in playerDTOList) {
            val playerStanding = groupStanding.find { it.player[0].id == player[0].id } ?: throw RuntimeException()
            Assertions.assertEquals(counter, playerStanding.groupPosition)
            counter += 1
        }
    }

    /**
     * Player 1 wins 2, players 2 wins 2, player 3 wins 1, player 4 wins 1
     * But player 1 beats player 2, and player 4 beats player 3, so not set or point count
     */
    @Test
    fun testMutualMeetings() {
        // Setup
        winAgainstPlayersX(
            uniquePlayerRegistrations.elementAt(0),
            mutableListOf(
                uniquePlayerRegistrations.elementAt(1),
                uniquePlayerRegistrations.elementAt(3),
            )
        )

        winAgainstPlayersX(
            uniquePlayerRegistrations.elementAt(1),
            mutableListOf(
                uniquePlayerRegistrations.elementAt(2),
                uniquePlayerRegistrations.elementAt(3),
            )
        )

        winAgainstPlayersX(
            uniquePlayerRegistrations.elementAt(2),
            mutableListOf(
                uniquePlayerRegistrations.elementAt(0),
            )
        )
        winAgainstPlayersX(
            uniquePlayerRegistrations.elementAt(3),
            mutableListOf(
                uniquePlayerRegistrations.elementAt(2),
            )
        )

        val playerDTOList: MutableList<List<PlayerDTO>> = mutableListOf()
        for (registration in uniquePlayerRegistrations) {
            playerDTOList.add(registrationRepository.getPlayersFrom(registration))
        }

        // Act
        val groupStanding = calculateGroupStanding.execute(competitionCategoryId, "A")

        // Assert
        Assertions.assertEquals(1, groupStanding.find{it.player[0].id == playerDTOList[0][0].id}?.groupPosition)
        Assertions.assertEquals(2, groupStanding.find{it.player[0].id == playerDTOList[1][0].id}?.groupPosition)
        Assertions.assertEquals(3, groupStanding.find{it.player[0].id == playerDTOList[3][0].id}?.groupPosition)
        Assertions.assertEquals(4, groupStanding.find{it.player[0].id == playerDTOList[2][0].id}?.groupPosition)
    }
    /**
     * Player 1 wins 3, players 2 wins 1, player 3 wins 1, player 4 wins 1
     * But player 2 beats player 3, player 3 beats player 4, player 4 beats player 2
     */
    @Test
    fun testMutualMeetingsThreeWayTie() {
        // Setup
        winAgainstPlayersX(
            uniquePlayerRegistrations.elementAt(0),
            mutableListOf(
                uniquePlayerRegistrations.elementAt(1),
                uniquePlayerRegistrations.elementAt(2),
                uniquePlayerRegistrations.elementAt(3),
            )
        )

        winAgainstPlayersX(
            uniquePlayerRegistrations.elementAt(1),
            mutableListOf(
                uniquePlayerRegistrations.elementAt(2),
            )
        )

        winAgainstPlayersX(
            uniquePlayerRegistrations.elementAt(2),
            mutableListOf(
                uniquePlayerRegistrations.elementAt(3),
            )
        )
        winAgainstPlayersX(
            uniquePlayerRegistrations.elementAt(3),
            mutableListOf(
                uniquePlayerRegistrations.elementAt(1),
            )
        )

        val playerDTOList: MutableList<List<PlayerDTO>> = mutableListOf()
        for (registration in uniquePlayerRegistrations) {
            playerDTOList.add(registrationRepository.getPlayersFrom(registration))
        }

        // Act
        val groupStanding = calculateGroupStanding.execute(competitionCategoryId, "A")

        // Assert
        Assertions.assertEquals(1, groupStanding.find { it.player[0].id == playerDTOList[0][0].id }?.groupPosition)
    }

        private fun winAgainstPlayersX(mainPlayer: Int, playersToBeat: List<Int>) {
        val matches = matchRepository.getMatchesInCategory(competitionCategoryId)
        val matchesInGroupA = matches.filter { it.groupOrRound == "A" }
        // Select all matches where the main player is one of the parties and the other party is one of the players to beat
        val selectedMatches = matchesInGroupA.filter {
            (it.firstRegistrationId == mainPlayer || it.secondRegistrationId == mainPlayer)
                    && (playersToBeat.contains(it.firstRegistrationId) || playersToBeat.contains(it.secondRegistrationId))
                    && it.winner == null
        }

        for (match in selectedMatches) {
            if (match.firstRegistrationId == mainPlayer) {
                generateAndSaveResult(match, PlayerPosition.FIRST)
            } else if (match.secondRegistrationId == mainPlayer) {
                generateAndSaveResult(match, PlayerPosition.SECOND)
            }
        }
    }

    private fun generateAndSaveResult(match: MatchRecord, playerPosition: PlayerPosition) {
        val nrGames = Random.nextInt(3, 6)

        val winningPlayerResults = mutableListOf<Int>()
        while (winningPlayerResults.size < nrGames) {
            winningPlayerResults.add(Random.nextInt(0, 12))
        }
        while (winningPlayerResults.count { it == 11 } < 3) {
            val gameToRemove = Random.nextInt(0, nrGames)
            winningPlayerResults.removeAt(gameToRemove)
            val value = Random.nextInt(4, 12)
            winningPlayerResults.add(value)
        }
        val gameResults = mutableListOf<GameSpec>()
        for ((index, winningPlayerResult) in winningPlayerResults.withIndex()) {
            val otherPlayerResult = when (winningPlayerResult) {
                10 -> 12
                11 -> Random.nextInt(0, 10)
                else -> 11
            }
            if (playerPosition == PlayerPosition.FIRST) {
                gameResults.add(GameSpec(index + 1, winningPlayerResult, otherPlayerResult))
            } else {
                gameResults.add(GameSpec(index + 1, otherPlayerResult, winningPlayerResult))
            }
        }
        val resultSpec = ResultSpec(gameResults)
        resultService.addResult(match.id, resultSpec)

    }

    enum class PlayerPosition {
        FIRST, SECOND
    }
}