package com.graphite.competitionplanner.draw.domain.groupstanding

import com.graphite.competitionplanner.category.interfaces.ICategoryRepository
import com.graphite.competitionplanner.club.interfaces.IClubRepository
import com.graphite.competitionplanner.competition.domain.FindCompetitions
import com.graphite.competitionplanner.competition.interfaces.ICompetitionRepository
import com.graphite.competitionplanner.competitioncategory.domain.UpdateCompetitionCategory
import com.graphite.competitionplanner.competitioncategory.interfaces.CompetitionCategoryUpdateSpec
import com.graphite.competitionplanner.competitioncategory.interfaces.GeneralSettingsDTO
import com.graphite.competitionplanner.competitioncategory.interfaces.ICompetitionCategoryRepository
import com.graphite.competitionplanner.draw.domain.CalculateGroupStanding
import com.graphite.competitionplanner.draw.domain.CreateDraw
import com.graphite.competitionplanner.draw.interfaces.SortedBy
import com.graphite.competitionplanner.match.repository.MatchRepository
import com.graphite.competitionplanner.player.interfaces.IPlayerRepository
import com.graphite.competitionplanner.player.interfaces.PlayerDTO
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
class GroupsOfThree(
    @Autowired val util: Util,
    @Autowired val calculateGroupStanding: CalculateGroupStanding,
    @Autowired val createDraw: CreateDraw,
    @Autowired val findCompetitions: FindCompetitions,
    @Autowired val updateCompetitionCategory: UpdateCompetitionCategory,
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
    var competitionCategoryId = 0
    var uniquePlayerRegistrations: MutableSet<Int> = mutableSetOf()

    @BeforeEach
    fun setUpTestData() {
        val club = newClub()
        val competition = club.addCompetition()
        val competitionCategory = competition.addCompetitionCategory()
        competitionCategoryId = competitionCategory.id

        // Update to groups of three
        updateCompetitionCategory.execute(
            competitionCategoryId,
            CompetitionCategoryUpdateSpec(
                settings = GeneralSettingsDTO(
                    competitionCategory.settings.cost,
                    competitionCategory.settings.drawType,
                    3,
                    1,
                    competitionCategory.settings.poolDrawStrategy,
                ),
                gameSettings = competitionCategory.gameSettings
            )
        )

        val suffixes = listOf("A", "B", "C")
        val players = suffixes.map { club.addPlayer("Player$it") }
        val registrations = players.map { competitionCategory.registerPlayer(it) }

        createDraw.execute(competitionCategoryId)

        uniquePlayerRegistrations = registrations.map { it.id }.toMutableSet()
        Assertions.assertEquals(3, uniquePlayerRegistrations.size)
    }

    @Test
    fun testDistinctGroupScores() {
        val player1 = uniquePlayerRegistrations.elementAt(0)
        val player2 = uniquePlayerRegistrations.elementAt(1)
        val player3 = uniquePlayerRegistrations.elementAt(2)

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
                player3,
            ),
            competitionCategoryId
        )

        // Act
        val groupStanding = calculateGroupStanding.execute(competitionCategoryId, "A")

        // Assert
        val playerDTOList: MutableList<List<PlayerDTO>> = mutableListOf()
        for (registration in uniquePlayerRegistrations) {
            playerDTOList.add(registrationRepository.getPlayersFrom(registration))
        }

        Assertions.assertEquals(1, groupStanding.find { it.player[0].id == playerDTOList[0][0].id }?.groupPosition)
        Assertions.assertTrue(groupStanding.find { it.player[0].id == playerDTOList[0][0].id }?.sortedBy == SortedBy.MATCH_SCORE)

        Assertions.assertEquals(2, groupStanding.find { it.player[0].id == playerDTOList[1][0].id }?.groupPosition)
        Assertions.assertTrue(groupStanding.find { it.player[0].id == playerDTOList[1][0].id }?.sortedBy == SortedBy.MATCH_SCORE)

        Assertions.assertEquals(3, groupStanding.find { it.player[0].id == playerDTOList[2][0].id }?.groupPosition)
        Assertions.assertTrue(groupStanding.find { it.player[0].id == playerDTOList[2][0].id }?.sortedBy == SortedBy.MATCH_SCORE)
    }

    // Three way tie. Ensure sorted by quotients
    @Test
    fun testThreeWayTieDifferentQuotients() {
        val player1 = uniquePlayerRegistrations.elementAt(0)
        val player2 = uniquePlayerRegistrations.elementAt(1)
        val player3 = uniquePlayerRegistrations.elementAt(2)

        // Players beat each other, players 1 has best set quotient, then 3, then 2
        groupStandingUtil.beatPlayerWithExactScore(
            player1,
            player2,
            listOf(11, 11, 11),
            listOf(8, 5, 7),
            competitionCategoryId
        )
        groupStandingUtil.beatPlayerWithExactScore(
            player2,
            player3,
            listOf(11, 5, 11, 11),
            listOf(2, 11, 3, 4),
            competitionCategoryId
        )
        groupStandingUtil.beatPlayerWithExactScore(
            player3,
            player1,
            listOf(11, 6, 11, 11),
            listOf(2, 11, 1, 5),
            competitionCategoryId
        )
        // Results: Player 1: 4:3, Player2: 3:4, Player 3: 4:4

        // Act
        val groupStanding = calculateGroupStanding.execute(competitionCategoryId, "A")

        // Assert position and score
        val playerDTOList: MutableList<List<PlayerDTO>> = mutableListOf()
        for (registration in uniquePlayerRegistrations) {
            playerDTOList.add(registrationRepository.getPlayersFrom(registration))
        }
        Assertions.assertEquals(1, groupStanding.find { it.player[0].id == playerDTOList[0][0].id }?.groupPosition)
        Assertions.assertEquals(3, groupStanding.find { it.player[0].id == playerDTOList[0][0].id }?.groupScore)
        Assertions.assertEquals(3,groupStanding.find { it.player[0].id == playerDTOList[0][0].id }?.subgroupStanding?.groupScore)
        Assertions.assertTrue(groupStanding.find { it.player[0].id == playerDTOList[0][0].id }?.sortedBy == SortedBy.GAME_QUOTIENT)

        Assertions.assertEquals(2, groupStanding.find { it.player[0].id == playerDTOList[2][0].id }?.groupPosition)
        Assertions.assertEquals(3, groupStanding.find { it.player[0].id == playerDTOList[2][0].id }?.groupScore)
        Assertions.assertEquals(3,groupStanding.find { it.player[0].id == playerDTOList[2][0].id }?.subgroupStanding?.groupScore)
        Assertions.assertTrue(groupStanding.find { it.player[0].id == playerDTOList[2][0].id }?.sortedBy == SortedBy.GAME_QUOTIENT)

        Assertions.assertEquals(3, groupStanding.find { it.player[0].id == playerDTOList[1][0].id }?.groupPosition)
        Assertions.assertEquals(3, groupStanding.find { it.player[0].id == playerDTOList[1][0].id }?.groupScore)
        Assertions.assertEquals(3,groupStanding.find { it.player[0].id == playerDTOList[1][0].id }?.subgroupStanding?.groupScore)
        Assertions.assertTrue(groupStanding.find { it.player[0].id == playerDTOList[1][0].id }?.sortedBy == SortedBy.GAME_QUOTIENT)
    }

    // Tie where all have same number of games. Should be sorted by points
    @Test
    fun testThreeWayTieSameSets() {
        //Set up
        val player1 = uniquePlayerRegistrations.elementAt(0)
        val player2 = uniquePlayerRegistrations.elementAt(1)
        val player3 = uniquePlayerRegistrations.elementAt(2)


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
        Assertions.assertEquals(3,groupStanding.find { it.player[0].id == playerDTOList[2][0].id }?.subgroupStanding?.groupScore)
        Assertions.assertTrue(groupStanding.find { it.player[0].id == playerDTOList[2][0].id }?.sortedBy == SortedBy.POINT_QUOTIENT)

        Assertions.assertEquals(2, groupStanding.find { it.player[0].id == playerDTOList[1][0].id }?.groupPosition)
        Assertions.assertEquals(3,groupStanding.find { it.player[0].id == playerDTOList[1][0].id }?.subgroupStanding?.groupScore)
        Assertions.assertTrue(groupStanding.find { it.player[0].id == playerDTOList[1][0].id }?.sortedBy == SortedBy.POINT_QUOTIENT)

        Assertions.assertEquals(3, groupStanding.find { it.player[0].id == playerDTOList[0][0].id }?.groupPosition)
        Assertions.assertEquals(3,groupStanding.find { it.player[0].id == playerDTOList[0][0].id }?.subgroupStanding?.groupScore)
        Assertions.assertTrue(groupStanding.find { it.player[0].id == playerDTOList[0][0].id }?.sortedBy == SortedBy.POINT_QUOTIENT)
    }

    // Three way tie and all players have same set quotient and same points. Draw should settle
    @Test
    fun testThreeWayTieSameSets_SamePoints() {
        //Set up
        val player1 = uniquePlayerRegistrations.elementAt(0)
        val player2 = uniquePlayerRegistrations.elementAt(1)
        val player3 = uniquePlayerRegistrations.elementAt(2)

        // Players beat each other, all of them have 4-4 in sets. Player3 has better point quotient, then 2, then 1
        groupStandingUtil.beatPlayerWithExactScore(player1, player2, listOf(11, 11, 11), listOf(0, 0, 0), competitionCategoryId)
        groupStandingUtil.beatPlayerWithExactScore(player2, player3, listOf(11, 11, 11), listOf(0, 0, 0), competitionCategoryId)
        groupStandingUtil.beatPlayerWithExactScore(player3, player1, listOf(11, 11, 11), listOf(0, 0, 0), competitionCategoryId)

        // Act
        val groupStanding = calculateGroupStanding.execute(competitionCategoryId, "A")

        // Assert
        val playerDTOList: MutableList<List<PlayerDTO>> = mutableListOf()
        for (registration in uniquePlayerRegistrations) {
            playerDTOList.add(registrationRepository.getPlayersFrom(registration))
        }
        Assertions.assertTrue(groupStanding.find { it.player[0].id == playerDTOList[0][0].id }?.sortedBy == SortedBy.DRAW)
        Assertions.assertTrue(groupStanding.find { it.player[0].id == playerDTOList[1][0].id }?.sortedBy == SortedBy.DRAW)
        Assertions.assertTrue(groupStanding.find { it.player[0].id == playerDTOList[2][0].id }?.sortedBy == SortedBy.DRAW)
    }
}