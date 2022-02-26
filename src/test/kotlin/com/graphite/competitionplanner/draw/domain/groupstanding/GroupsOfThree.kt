package com.graphite.competitionplanner.draw.domain.groupstanding

import com.graphite.competitionplanner.competition.domain.FindCompetitions
import com.graphite.competitionplanner.competition.repository.CompetitionRepository
import com.graphite.competitionplanner.competitioncategory.domain.FindCompetitionCategory
import com.graphite.competitionplanner.competitioncategory.domain.UpdateCompetitionCategory
import com.graphite.competitionplanner.competitioncategory.interfaces.CompetitionCategoryStatus
import com.graphite.competitionplanner.competitioncategory.interfaces.CompetitionCategoryUpdateSpec
import com.graphite.competitionplanner.competitioncategory.interfaces.GeneralSettingsDTO
import com.graphite.competitionplanner.competitioncategory.interfaces.ICompetitionCategoryRepository
import com.graphite.competitionplanner.draw.domain.CalculateGroupStanding
import com.graphite.competitionplanner.draw.domain.CreateDraw
import com.graphite.competitionplanner.draw.interfaces.SortedBy
import com.graphite.competitionplanner.draw.repository.CompetitionDrawRepository
import com.graphite.competitionplanner.match.repository.MatchRepository
import com.graphite.competitionplanner.player.interfaces.PlayerDTO
import com.graphite.competitionplanner.player.repository.PlayerRepository
import com.graphite.competitionplanner.registration.domain.Withdraw
import com.graphite.competitionplanner.registration.interfaces.RegistrationSinglesSpec
import com.graphite.competitionplanner.registration.repository.RegistrationRepository
import com.graphite.competitionplanner.registration.service.RegistrationService
import com.graphite.competitionplanner.util.TestUtil
import com.graphite.competitionplanner.util.Util
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
class GroupsOfThree(

    @Autowired val competitionRepository: CompetitionRepository,
    @Autowired val util: Util,
    @Autowired val testUtil: TestUtil,
    @Autowired val playerRepository: PlayerRepository,
    @Autowired val registrationService: RegistrationService,
    @Autowired val matchRepository: MatchRepository,
    @Autowired val competitionDrawRepository: CompetitionDrawRepository,
    @Autowired val registrationRepository: RegistrationRepository,
    @Autowired val calculateGroupStanding: CalculateGroupStanding,
    @Autowired val createDraw: CreateDraw,
    @Autowired val withdraw: Withdraw,
    @Autowired val findCompetitions: FindCompetitions,
    @Autowired val findCompetitionCategory: FindCompetitionCategory,
    @Autowired val updateCompetitionCategory: UpdateCompetitionCategory,
    @Autowired val groupStandingUtil: GroupStandingUtil,
    @Autowired val competitionCategoryRepository: ICompetitionCategoryRepository
) {
    var competitionCategoryId = 0
    var uniquePlayerRegistrations: MutableSet<Int> = mutableSetOf()

    @BeforeAll
    fun setUpClassData() {
        try {
            competitionCategoryId = testUtil.addCompetitionCategory("Herrar 6")
        }
        catch (exception: IllegalArgumentException) {
            println("Category already added")
        }

        // Update to groups of three
        val originalCategory = findCompetitionCategory.byId(competitionCategoryId)
        updateCompetitionCategory.execute(
            competitionCategoryId,
            CompetitionCategoryUpdateSpec(
                settings = GeneralSettingsDTO(
                    originalCategory.settings.cost,
                    originalCategory.settings.drawType,
                    3,
                    1,
                    originalCategory.settings.poolDrawStrategy,
                ),
                gameSettings = originalCategory.gameSettings
            )
        )
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
        Assertions.assertEquals(3, uniquePlayerRegistrations.size)
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

        // Delete pools
        competitionDrawRepository.deletePools(competitionCategoryId)

        // Set status to Active
        competitionCategoryRepository.setStatus(competitionCategoryId, CompetitionCategoryStatus.ACTIVE)
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