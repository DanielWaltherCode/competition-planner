package com.graphite.competitionplanner.draw.domain.groupstanding

import com.graphite.competitionplanner.category.domain.DefaultCategory
import com.graphite.competitionplanner.category.interfaces.ICategoryRepository
import com.graphite.competitionplanner.club.interfaces.IClubRepository
import com.graphite.competitionplanner.competition.domain.FindCompetitions
import com.graphite.competitionplanner.competition.interfaces.ICompetitionRepository
import com.graphite.competitionplanner.competitioncategory.domain.GetCompetitionCategories
import com.graphite.competitionplanner.competitioncategory.interfaces.ICompetitionCategoryRepository
import com.graphite.competitionplanner.draw.domain.CalculateGroupStanding
import com.graphite.competitionplanner.draw.domain.CreateDraw
import com.graphite.competitionplanner.draw.domain.GetDraw
import com.graphite.competitionplanner.draw.interfaces.SortedBy
import com.graphite.competitionplanner.match.repository.MatchRepository
import com.graphite.competitionplanner.player.interfaces.IPlayerRepository
import com.graphite.competitionplanner.player.interfaces.PlayerDTO
import com.graphite.competitionplanner.registration.domain.Withdraw
import com.graphite.competitionplanner.registration.interfaces.IRegistrationRepository
import com.graphite.competitionplanner.result.interfaces.IResultRepository
import com.graphite.competitionplanner.util.BaseRepositoryTest
import com.graphite.competitionplanner.util.Util
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class GroupsOfFour(
    @Autowired val getDraw: GetDraw,
    @Autowired val getCompetitionCategories: GetCompetitionCategories,
    @Autowired val util: Util,
    @Autowired val calculateGroupStanding: CalculateGroupStanding,
    @Autowired val createDraw: CreateDraw,
    @Autowired val withdraw: Withdraw,
    @Autowired val findCompetitions: FindCompetitions,
    @Autowired val groupStandingUtil: GroupStandingUtil,
    @Autowired competitionCategoryRepository: ICompetitionCategoryRepository,
    @Autowired clubRepository: IClubRepository,
    @Autowired competitionRepository: ICompetitionRepository,
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

    var competitionId = 0
    var competitionCategoryId = 0
    var uniquePlayerRegistrations: MutableSet<Int> = mutableSetOf()

    @BeforeEach
    fun setUpTestData() {
        val club = newClub()
        val competition = club.addCompetition()
        competitionId = competition.id
        val competitionCategory = competition.addCompetitionCategory(DefaultCategory.MEN_6.name)
        val suffixes = listOf("A", "B", "C", "D")
        val players = suffixes.map { club.addPlayer("Player$it") }
        competitionCategoryId = competitionCategory.id
        val registrations = players.map { competitionCategory.registerPlayer(it) }

        createDraw.execute(competitionCategoryId)

        uniquePlayerRegistrations = registrations.map { it.id }.toMutableSet()
        Assertions.assertEquals(4, uniquePlayerRegistrations.size)
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
        groupStandingUtil.winAgainstPlayersX(
            player1,
            mutableListOf(
                player2, player3, player4
            ),
            competitionCategoryId
        )

        groupStandingUtil.winAgainstPlayersX(
            player2,
            mutableListOf(
                player3, player4
            ),
            competitionCategoryId
        )

        groupStandingUtil.winAgainstPlayersX(
            player3,
            mutableListOf(
                    player4
            ),
            competitionCategoryId
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
        groupStandingUtil.winAgainstPlayersX(
            player1,
            mutableListOf(
                player2, player4
            ),
            competitionCategoryId
        )

        groupStandingUtil.winAgainstPlayersX(
            player2,
            mutableListOf(
                player3, player4
            ), competitionCategoryId
        )

        groupStandingUtil.winAgainstPlayersX(
            player3,
            mutableListOf(
                player1
            ),
            competitionCategoryId
        )
        groupStandingUtil.winAgainstPlayersX(
            player4,
            mutableListOf(
                player3
            ),
            competitionCategoryId
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
        groupStandingUtil.winAgainstPlayersX(
            player1,
            mutableListOf(
                player2, player3, player4
            ),
            competitionCategoryId
        )

        groupStandingUtil.winAgainstPlayersX(
            player2,
            mutableListOf(
                player3
            ),
            competitionCategoryId
        )

        groupStandingUtil.winAgainstPlayersX(
            player3,
            mutableListOf(
                player4
            ),
            competitionCategoryId
        )
        groupStandingUtil.winAgainstPlayersX(
            player4,
            mutableListOf(
                player2
            ),
            competitionCategoryId
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
        withdraw.walkOver(competitionId, competitionCategoryId, player4)

        // Setup
        groupStandingUtil.winAgainstPlayersX(
            player1,
            mutableListOf(
                player2, player3
            ),
            competitionCategoryId
        )

        groupStandingUtil.winAgainstPlayersX(
            player2,
            mutableListOf(
                player3, player4
            ),
            competitionCategoryId
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

    @Test
    fun testGroupWithWalkoverAfterSomeMatchesPlayed() {
        //Set up
        val player1 = uniquePlayerRegistrations.elementAt(0)
        val player2 = uniquePlayerRegistrations.elementAt(1)
        val player3 = uniquePlayerRegistrations.elementAt(2)
        val player4 = uniquePlayerRegistrations.elementAt(3)

        // Setup
        groupStandingUtil.winAgainstPlayersX(
            player1,
            mutableListOf(
                player2, player3, player4
            ),
            competitionCategoryId
        )

        withdraw.walkOver(competitionId, competitionCategoryId, player4)

        groupStandingUtil.winAgainstPlayersX(
            player2,
            mutableListOf(
                player3
            ),
            competitionCategoryId
        )


        // Act
        val groupStanding = calculateGroupStanding.execute(competitionCategoryId, "A")
        val playerDTOList: MutableList<List<PlayerDTO>> = mutableListOf()
        for (registration in uniquePlayerRegistrations) {
            playerDTOList.add(registrationRepository.getPlayersFrom(registration))
        }
        // Assert
        // Player four should be last due to WO in 2 matches. Player 3 lost all matches but still gets more points
        // than the WO player. WO player gets one point for losing one match.
        Assertions.assertEquals(1, groupStanding.find { it.player[0].id == playerDTOList[0][0].id }?.groupPosition)
        Assertions.assertEquals(6, groupStanding.find { it.player[0].id == playerDTOList[0][0].id }?.groupScore)

        Assertions.assertEquals(2, groupStanding.find { it.player[0].id == playerDTOList[1][0].id }?.groupPosition)
        Assertions.assertEquals(5, groupStanding.find { it.player[0].id == playerDTOList[1][0].id }?.groupScore)

        Assertions.assertEquals(3, groupStanding.find { it.player[0].id == playerDTOList[2][0].id }?.groupPosition)
        Assertions.assertEquals(4, groupStanding.find { it.player[0].id == playerDTOList[2][0].id }?.groupScore)

        Assertions.assertEquals(4, groupStanding.find { it.player[0].id == playerDTOList[3][0].id }?.groupPosition)
        Assertions.assertEquals(1, groupStanding.find { it.player[0].id == playerDTOList[3][0].id }?.groupScore)
    }

    // Three way tie. Ensure sorted by game quotients
    @Test
    fun testThreeWayTieDifferentQuotients() {
        val player1 = uniquePlayerRegistrations.elementAt(0)
        val player2 = uniquePlayerRegistrations.elementAt(1)
        val player3 = uniquePlayerRegistrations.elementAt(2)
        val player4 = uniquePlayerRegistrations.elementAt(3)
        // All beat fourth player
        for (i in 0..2) {
            groupStandingUtil.winAgainstPlayersX(
                uniquePlayerRegistrations.elementAt(i),
                mutableListOf(
                    player4
                ),
                competitionCategoryId
            )
        }

        // Players beat each other, players 1 has best game quotient, then 3, then 2
        groupStandingUtil.beatPlayerWithExactScore(player1, player2, listOf(11, 11, 11), listOf(8, 5, 7), competitionCategoryId)
        groupStandingUtil.beatPlayerWithExactScore(player2, player3, listOf(11, 5, 11, 11), listOf(2, 11, 3, 4), competitionCategoryId)
        groupStandingUtil.beatPlayerWithExactScore(player3, player1, listOf(11, 6, 11, 11), listOf(2, 11, 1, 5), competitionCategoryId)
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
            groupStandingUtil.winAgainstPlayersX(
                uniquePlayerRegistrations.elementAt(i),
                mutableListOf(
                    player4
                ),
                competitionCategoryId
            )
        }

        // Players beat each other, all of them have 4-4 in sets. Player3 has better point quotient, then 2, then 1
        groupStandingUtil.beatPlayerWithExactScore(player1, player2, listOf(11, 11, 8, 11), listOf(7, 7, 11, 7), competitionCategoryId)
        groupStandingUtil.beatPlayerWithExactScore(player2, player3, listOf(11, 5, 11, 11), listOf(9, 11, 3, 4), competitionCategoryId)
        groupStandingUtil.beatPlayerWithExactScore(player3, player1, listOf(11, 6, 11, 11), listOf(2, 11, 1, 5), competitionCategoryId)
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
            groupStandingUtil.winAgainstPlayersX(
                uniquePlayerRegistrations.elementAt(i),
                mutableListOf(
                    player4
                ),
                competitionCategoryId
            )
        }

        // Players beat each other, all of them have 4-4 in sets. Player3 has better point quotient, then 2, then 1
        groupStandingUtil.beatPlayerWithExactScore(player1, player2, listOf(11, 11, 11), listOf(0, 0, 0), competitionCategoryId)
        groupStandingUtil.beatPlayerWithExactScore(player2, player3, listOf(11, 11, 11), listOf(0, 0, 0), competitionCategoryId)
        groupStandingUtil.beatPlayerWithExactScore(player3, player1, listOf(11, 11, 11), listOf(0, 0, 0), competitionCategoryId)

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


}