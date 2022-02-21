package com.graphite.competitionplanner.draw.domain

import com.graphite.competitionplanner.competition.domain.FindCompetitions
import com.graphite.competitionplanner.competition.repository.CompetitionRepository
import com.graphite.competitionplanner.competitioncategory.domain.DeleteCompetitionCategory
import com.graphite.competitionplanner.competitioncategory.domain.GetCompetitionCategories
import com.graphite.competitionplanner.draw.interfaces.SortedBy
import com.graphite.competitionplanner.draw.repository.CompetitionDrawRepository
import com.graphite.competitionplanner.match.repository.MatchRepository
import com.graphite.competitionplanner.player.interfaces.PlayerDTO
import com.graphite.competitionplanner.player.repository.PlayerRepository
import com.graphite.competitionplanner.registration.domain.Withdraw
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
    @Autowired val matchRepository: MatchRepository,
    @Autowired val competitionDrawRepository: CompetitionDrawRepository,
    @Autowired val registrationRepository: RegistrationRepository,
    @Autowired val resultService: ResultService,
    @Autowired val calculateGroupStanding: CalculateGroupStanding,
    @Autowired val createDraw: CreateDraw,
    @Autowired val withdraw: Withdraw,
    @Autowired val findCompetitions: FindCompetitions,
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

        // Remove registrations
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
        val player1 = uniquePlayerRegistrations.elementAt(0)
        val player2 = uniquePlayerRegistrations.elementAt(1)
        val player3 = uniquePlayerRegistrations.elementAt(2)
        val player4 = uniquePlayerRegistrations.elementAt(3)
        // Setup
        winAgainstPlayersX(
            player1,
            mutableListOf(
                player2, player3, player4
            )
        )

        winAgainstPlayersX(
            player2,
            mutableListOf(
                player3, player4
            )
        )

        winAgainstPlayersX(
            player3,
            mutableListOf(
                    player4
            )
        )

        // Act
        val groupStanding = calculateGroupStanding.execute(competitionCategoryId, "A")

        // Assert
        var counter = 1
        val playerDTOList: MutableList<List<PlayerDTO>> = mutableListOf()
        for (registration in uniquePlayerRegistrations) {
            playerDTOList.add(registrationRepository.getPlayersFrom(registration))
        }
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
        val player1 = uniquePlayerRegistrations.elementAt(0)
        val player2 = uniquePlayerRegistrations.elementAt(1)
        val player3 = uniquePlayerRegistrations.elementAt(2)
        val player4 = uniquePlayerRegistrations.elementAt(3)

        // Setup
        winAgainstPlayersX(
            player1,
            mutableListOf(
                player2, player4
            )
        )

        winAgainstPlayersX(
            player2,
            mutableListOf(
                player3, player4
            )
        )

        winAgainstPlayersX(
            player3,
            mutableListOf(
                player1
            )
        )
        winAgainstPlayersX(
            player4,
            mutableListOf(
                player3
            )
        )

        // Act
        val groupStanding = calculateGroupStanding.execute(competitionCategoryId, "A")
        val playerDTOList: MutableList<List<PlayerDTO>> = mutableListOf()
        for (registration in uniquePlayerRegistrations) {
            playerDTOList.add(registrationRepository.getPlayersFrom(registration))
        }
        // Assert
        // Group position 1, there is a subgroupstanding (that is, we had to create and calculate a subgroup to arrive at player's position)
        // And the player won the subgroup match, i.e. 2 points
        Assertions.assertEquals(1, groupStanding.find { it.player[0].id == playerDTOList[0][0].id }?.groupPosition)
        Assertions.assertNotNull(groupStanding.find { it.player[0].id == playerDTOList[0][0].id }?.subgroupStanding)
        Assertions.assertEquals(2, groupStanding.find { it.player[0].id == playerDTOList[0][0].id }?.subgroupStanding?.groupScore)
        Assertions.assertTrue(groupStanding.find { it.player[0].id == playerDTOList[0][0].id }?.sortedBy == SortedBy.MATCH_SCORE)

        Assertions.assertEquals(2, groupStanding.find { it.player[0].id == playerDTOList[1][0].id }?.groupPosition)
        Assertions.assertNotNull(groupStanding.find { it.player[0].id == playerDTOList[1][0].id }?.subgroupStanding)
        Assertions.assertEquals(1, groupStanding.find { it.player[0].id == playerDTOList[1][0].id }?.subgroupStanding?.groupScore)
        Assertions.assertTrue(groupStanding.find { it.player[0].id == playerDTOList[1][0].id }?.sortedBy == SortedBy.MATCH_SCORE)


        Assertions.assertEquals(3, groupStanding.find { it.player[0].id == playerDTOList[3][0].id }?.groupPosition)
        Assertions.assertNotNull(groupStanding.find { it.player[0].id == playerDTOList[3][0].id }?.subgroupStanding)
        Assertions.assertEquals(2, groupStanding.find { it.player[0].id == playerDTOList[3][0].id }?.subgroupStanding?.groupScore)
        Assertions.assertTrue(groupStanding.find { it.player[0].id == playerDTOList[3][0].id }?.sortedBy == SortedBy.MATCH_SCORE)

        Assertions.assertEquals(4, groupStanding.find { it.player[0].id == playerDTOList[2][0].id }?.groupPosition)
        Assertions.assertNotNull(groupStanding.find { it.player[0].id == playerDTOList[2][0].id }?.subgroupStanding)
        Assertions.assertEquals(1, groupStanding.find { it.player[0].id == playerDTOList[2][0].id }?.subgroupStanding?.groupScore)
        Assertions.assertTrue(groupStanding.find { it.player[0].id == playerDTOList[2][0].id }?.sortedBy == SortedBy.MATCH_SCORE)
    }

    /**
     * Player 1 wins 3, players 2 wins 1, player 3 wins 1, player 4 wins 1
     * But player 2 beats player 3, player 3 beats player 4, player 4 beats player 2
     */
    @Test
    fun testMutualMeetingsThreeWayTie() {
        val player1 = uniquePlayerRegistrations.elementAt(0)
        val player2 = uniquePlayerRegistrations.elementAt(1)
        val player3 = uniquePlayerRegistrations.elementAt(2)
        val player4 = uniquePlayerRegistrations.elementAt(3)

        // Setup
        winAgainstPlayersX(
            player1,
            mutableListOf(
                player2, player3, player4
            )
        )

        winAgainstPlayersX(
            player2,
            mutableListOf(
                player3
            )
        )

        winAgainstPlayersX(
            player3,
            mutableListOf(
                player4
            )
        )
        winAgainstPlayersX(
            player4,
            mutableListOf(
                player2
            )
        )

        // Act
        val groupStanding = calculateGroupStanding.execute(competitionCategoryId, "A")
        val playerDTOList: MutableList<List<PlayerDTO>> = mutableListOf()
        for (registration in uniquePlayerRegistrations) {
            playerDTOList.add(registrationRepository.getPlayersFrom(registration))
        }
        // Assert
        Assertions.assertEquals(1, groupStanding.find { it.player[0].id == playerDTOList[0][0].id }?.groupPosition)
    }

    @Test
    fun testGroupWithWalkoverBeforeMatchesPlayed() {
        //Set up
        val player1 = uniquePlayerRegistrations.elementAt(0)
        val player2 = uniquePlayerRegistrations.elementAt(1)
        val player3 = uniquePlayerRegistrations.elementAt(2)
        val player4 = uniquePlayerRegistrations.elementAt(3)
        val umeaId = util.getClubIdOrDefault("Umeå IK")
        val umeaCompetitions = findCompetitions.thatBelongTo(umeaId)
        val umeaCompetitionId = umeaCompetitions[0].id
        withdraw.walkOver(umeaCompetitionId, competitionCategoryId, player4)

        // Setup
        winAgainstPlayersX(
            player1,
            mutableListOf(
                player2, player3
            )
        )

        winAgainstPlayersX(
            player2,
            mutableListOf(
                player3,
            )
        )

        // Act
        val groupStanding = calculateGroupStanding.execute(competitionCategoryId, "A")
        val playerDTOList: MutableList<List<PlayerDTO>> = mutableListOf()
        for (registration in uniquePlayerRegistrations) {
            playerDTOList.add(registrationRepository.getPlayersFrom(registration))
        }
        // Assert
        // Player four should be last due to WO in all matches. Player 3 lost all matches but still gets more points
        // than the WO player
        Assertions.assertEquals(1, groupStanding.find { it.player[0].id == playerDTOList[0][0].id }?.groupPosition)
        Assertions.assertEquals(6, groupStanding.find { it.player[0].id == playerDTOList[0][0].id }?.groupScore)

        Assertions.assertEquals(2, groupStanding.find { it.player[0].id == playerDTOList[1][0].id }?.groupPosition)
        Assertions.assertEquals(5, groupStanding.find { it.player[0].id == playerDTOList[1][0].id }?.groupScore)

        Assertions.assertEquals(3, groupStanding.find { it.player[0].id == playerDTOList[2][0].id }?.groupPosition)
        Assertions.assertEquals(4, groupStanding.find { it.player[0].id == playerDTOList[2][0].id }?.groupScore)

        Assertions.assertEquals(4, groupStanding.find { it.player[0].id == playerDTOList[3][0].id }?.groupPosition)
        Assertions.assertEquals(0, groupStanding.find { it.player[0].id == playerDTOList[3][0].id }?.groupScore)
    }

    // Three way tie. Ensure sorted by quotients
    @Test
    fun testThreeWayTieDifferentQuotients() {
        val player1 = uniquePlayerRegistrations.elementAt(0)
        val player2 = uniquePlayerRegistrations.elementAt(1)
        val player3 = uniquePlayerRegistrations.elementAt(2)
        val player4 = uniquePlayerRegistrations.elementAt(3)
        // All beat fourth player
        for (i in 0..2) {
            winAgainstPlayersX(
                uniquePlayerRegistrations.elementAt(i),
                mutableListOf(
                    player4
                )
            )
        }

        // Players beat each other, players 1 has best set quotient, then 3, then 2
        beatPlayerWithExactScore(player1, player2, listOf(11, 11, 11), listOf(8, 5, 7))
        beatPlayerWithExactScore(player2, player3, listOf(11, 5, 11, 11), listOf(2, 11, 3, 4))
        beatPlayerWithExactScore(player3, player1, listOf(11, 6, 11, 11), listOf(2, 11, 1, 5))
        // Results: Player 1: 4:3, Player2: 3:4, Player 3: 4:4

        // Act
        val groupStanding = calculateGroupStanding.execute(competitionCategoryId, "A")

        // Assert position and score
        val playerDTOList: MutableList<List<PlayerDTO>> = mutableListOf()
        for (registration in uniquePlayerRegistrations) {
            playerDTOList.add(registrationRepository.getPlayersFrom(registration))
        }
        Assertions.assertEquals(1, groupStanding.find { it.player[0].id == playerDTOList[0][0].id }?.groupPosition)
        Assertions.assertEquals(5, groupStanding.find { it.player[0].id == playerDTOList[0][0].id }?.groupScore)
        Assertions.assertTrue(groupStanding.find { it.player[0].id == playerDTOList[0][0].id }?.sortedBy == SortedBy.GAME_QUOTIENT)

        Assertions.assertEquals(2, groupStanding.find { it.player[0].id == playerDTOList[2][0].id }?.groupPosition)
        Assertions.assertEquals(5, groupStanding.find { it.player[0].id == playerDTOList[2][0].id }?.groupScore)
        Assertions.assertTrue(groupStanding.find { it.player[0].id == playerDTOList[2][0].id }?.sortedBy == SortedBy.GAME_QUOTIENT)

        Assertions.assertEquals(3, groupStanding.find { it.player[0].id == playerDTOList[1][0].id }?.groupPosition)
        Assertions.assertEquals(5, groupStanding.find { it.player[0].id == playerDTOList[1][0].id }?.groupScore)
        Assertions.assertTrue(groupStanding.find { it.player[0].id == playerDTOList[1][0].id }?.sortedBy == SortedBy.GAME_QUOTIENT)

        Assertions.assertEquals(4, groupStanding.find { it.player[0].id == playerDTOList[3][0].id }?.groupPosition)
        Assertions.assertEquals(3, groupStanding.find { it.player[0].id == playerDTOList[3][0].id }?.groupScore)
        Assertions.assertTrue(groupStanding.find { it.player[0].id == playerDTOList[3][0].id }?.sortedBy == SortedBy.MATCH_SCORE)
    }

    // Three way tie and all players have same set quotient
    @Test
    fun testThreeWayTieSameSets() {
    //Set up
        val player1 = uniquePlayerRegistrations.elementAt(0)
        val player2 = uniquePlayerRegistrations.elementAt(1)
        val player3 = uniquePlayerRegistrations.elementAt(2)
        val player4 = uniquePlayerRegistrations.elementAt(3)

        // All beat fourth player
        for (i in 0..2) {
            winAgainstPlayersX(
                uniquePlayerRegistrations.elementAt(i),
                mutableListOf(
                    player4
                )
            )
        }

        // Players beat each other, all of them have 4-4 in sets. Player3 has better point quotient, then 2, then 1
        beatPlayerWithExactScore(player1, player2, listOf(11, 11, 8, 11), listOf(7, 7, 11, 7))
        beatPlayerWithExactScore(player2, player3, listOf(11, 5, 11, 11), listOf(9, 11, 3, 4))
        beatPlayerWithExactScore(player3, player1, listOf(11, 6, 11, 11), listOf(2, 11, 1, 5))
        // Players total points: 1: 60:71, 2: 70:68, 3: 66:57

        // Act
        val groupStanding = calculateGroupStanding.execute(competitionCategoryId, "A")

        // Assert
        val playerDTOList: MutableList<List<PlayerDTO>> = mutableListOf()
        for (registration in uniquePlayerRegistrations) {
            playerDTOList.add(registrationRepository.getPlayersFrom(registration))
        }
        Assertions.assertEquals(1, groupStanding.find { it.player[0].id == playerDTOList[2][0].id }?.groupPosition)
        Assertions.assertTrue(groupStanding.find { it.player[0].id == playerDTOList[2][0].id }?.sortedBy == SortedBy.POINT_QUOTIENT)

        Assertions.assertEquals(2, groupStanding.find { it.player[0].id == playerDTOList[1][0].id }?.groupPosition)
        Assertions.assertTrue(groupStanding.find { it.player[0].id == playerDTOList[1][0].id }?.sortedBy == SortedBy.POINT_QUOTIENT)

        Assertions.assertEquals(3, groupStanding.find { it.player[0].id == playerDTOList[0][0].id }?.groupPosition)
        Assertions.assertTrue(groupStanding.find { it.player[0].id == playerDTOList[0][0].id }?.sortedBy == SortedBy.POINT_QUOTIENT)

        Assertions.assertEquals(4, groupStanding.find { it.player[0].id == playerDTOList[3][0].id }?.groupPosition)
        Assertions.assertTrue(groupStanding.find { it.player[0].id == playerDTOList[3][0].id }?.sortedBy == SortedBy.MATCH_SCORE)
    }

    // Three way tie and all players have same set quotient and same points. Draw should settle
    @Test
    fun testThreeWayTieSameSets_SamePoints() {
    //Set up
        val player1 = uniquePlayerRegistrations.elementAt(0)
        val player2 = uniquePlayerRegistrations.elementAt(1)
        val player3 = uniquePlayerRegistrations.elementAt(2)
        val player4 = uniquePlayerRegistrations.elementAt(3)

        // All beat fourth player
        for (i in 0..2) {
            winAgainstPlayersX(
                uniquePlayerRegistrations.elementAt(i),
                mutableListOf(
                    player4
                )
            )
        }

        // Players beat each other, all of them have 4-4 in sets. Player3 has better point quotient, then 2, then 1
        beatPlayerWithExactScore(player1, player2, listOf(11, 11, 11), listOf(0, 0, 0))
        beatPlayerWithExactScore(player2, player3, listOf(11, 11, 11), listOf(0, 0, 0))
        beatPlayerWithExactScore(player3, player1, listOf(11, 11, 11), listOf(0, 0, 0))

        // Act
        val groupStanding = calculateGroupStanding.execute(competitionCategoryId, "A")

        // Assert (can only assert last place since rest is random and sort type). Test still useful to ensure we don't get exceptions
        val playerDTOList: MutableList<List<PlayerDTO>> = mutableListOf()
        for (registration in uniquePlayerRegistrations) {
            playerDTOList.add(registrationRepository.getPlayersFrom(registration))
        }
        Assertions.assertEquals(4, groupStanding.find { it.player[0].id == playerDTOList[3][0].id }?.groupPosition)
        Assertions.assertTrue(groupStanding.find { it.player[0].id == playerDTOList[0][0].id }?.sortedBy == SortedBy.DRAW)
        Assertions.assertTrue(groupStanding.find { it.player[0].id == playerDTOList[1][0].id }?.sortedBy == SortedBy.DRAW)
        Assertions.assertTrue(groupStanding.find { it.player[0].id == playerDTOList[2][0].id }?.sortedBy == SortedBy.DRAW)
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

    private fun beatPlayerWithExactScore(
        mainPlayer: Int,
        playerToBeat: Int,
        mainPlayerResults: List<Int>,
        playerToBeatResults: List<Int>
    ) {
        val matches = matchRepository.getMatchesInCategory(competitionCategoryId)
        val matchesInGroupA = matches.filter { it.groupOrRound == "A" }

        // Select their match
        val match = matchesInGroupA.first {
            (it.firstRegistrationId == mainPlayer || it.secondRegistrationId == mainPlayer)
                    && (playerToBeat == it.firstRegistrationId || playerToBeat == it.secondRegistrationId)
                    && it.winner == null
        }

        val gameResults: MutableList<GameSpec> = mutableListOf()
        if (match.firstRegistrationId == mainPlayer) {
            for (i in mainPlayerResults.indices) {
                gameResults.add(GameSpec(i + 1, mainPlayerResults[i], playerToBeatResults[i]))
            }
        } else if (match.secondRegistrationId == mainPlayer) {
            for (i in mainPlayerResults.indices) {
                gameResults.add(GameSpec(i + 1, playerToBeatResults[i], mainPlayerResults[i]))
            }
        }
        resultService.addResult(match.id, ResultSpec(gameResults))
    }

    enum class PlayerPosition {
        FIRST, SECOND
    }
}