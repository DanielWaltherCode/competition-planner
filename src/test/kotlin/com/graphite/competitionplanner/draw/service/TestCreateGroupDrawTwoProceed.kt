package com.graphite.competitionplanner.draw.service

import com.graphite.competitionplanner.competitioncategory.service.CompetitionCategoryService
import com.graphite.competitionplanner.domain.entity.Round
import com.graphite.competitionplanner.draw.repository.CompetitionDrawRepository
import com.graphite.competitionplanner.match.service.MatchService
import com.graphite.competitionplanner.player.repository.PlayerRepository
import com.graphite.competitionplanner.registration.interfaces.RegistrationSinglesSpec
import com.graphite.competitionplanner.registration.repository.RegistrationRepository
import com.graphite.competitionplanner.registration.service.RegistrationService
import com.graphite.competitionplanner.util.TestUtil
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestCreateGroupDrawTwoProceed(
    @Autowired val drawService: DrawService,
    @Autowired val competitionCategoryService: CompetitionCategoryService,
    @Autowired val testUtil: TestUtil,
    @Autowired val playerRepository: PlayerRepository,
    @Autowired val registrationRepository: RegistrationRepository,
    @Autowired val competitionDrawRepository: CompetitionDrawRepository,
    @Autowired val registrationService: RegistrationService,
    @Autowired val matchService: MatchService
) {

    var competitionCategoryId = 0

    @BeforeEach
    fun addPlayersToCategory() {
        competitionCategoryId = testUtil.addCompetitionCategory("Flickor 13")
    }

    @AfterEach
    fun cleanUp() {
        // Remove matches
        matchService.deleteMatchesInCategory(competitionCategoryId)

        // Remove pool draw
        competitionDrawRepository.deleteGroupsInCategory(competitionCategoryId)

        // Remove registrations and delete category
        val registrationIds = registrationRepository.getRegistrationIdsInCategory(competitionCategoryId)
        for (id in registrationIds) {
            registrationService.unregister(id)
        }
        competitionCategoryService.deleteCategoryInCompetition(competitionCategoryId)
    }

    @Test
    fun testMakeDraw22() {
        val allPlayers = playerRepository.getAll()

        // Get 22 players
        val players = allPlayers.subList(0, 22)
        for (player in players) {
            registrationService.registerPlayerSingles(RegistrationSinglesSpec( player.id, competitionCategoryId))
        }
        val draw = drawService.createDraw(competitionCategoryId)
        // With 22 players there should be six groups, 6 matches in the first 4, 3 in the two last ones
        Assertions.assertNotNull(draw)
        val groups = draw.groupDraw.groups
        Assertions.assertEquals(6, groups.size)
        Assertions.assertEquals(6, groups[0].matches.size)
        Assertions.assertEquals("A", groups[0].groupName)
        Assertions.assertEquals(6, groups[1].matches.size)
        Assertions.assertEquals("B", groups[1].groupName)
        Assertions.assertEquals(6, groups[2].matches.size)
        Assertions.assertEquals("C", groups[2].groupName)
        Assertions.assertEquals(6, groups[3].matches.size)
        Assertions.assertEquals("D", groups[3].groupName)
        Assertions.assertEquals(3, groups[4].matches.size)
        Assertions.assertEquals("E", groups[4].groupName)
        Assertions.assertEquals(3, groups[5].matches.size)
        Assertions.assertEquals("F", groups[5].groupName)

        val playoffMatches = draw.playOff.rounds[0].matches
        val playoffRound = draw.playOff.rounds[0].round

        Assertions.assertEquals(Round.ROUND_OF_16, playoffRound)
        Assertions.assertEquals(playoffMatches.size, 8)
    }

    @Test
    fun testMakeDraw20() {
        val allPlayers = playerRepository.getAll()

        // Get 20 players
        val players = allPlayers.subList(0, 20)
        for (player in players) {
            registrationService.registerPlayerSingles(RegistrationSinglesSpec( player.id, competitionCategoryId))
        }
        val draw = drawService.createDraw(competitionCategoryId)

        // With 20 players there should be exactly 5 groups
        val groups = draw.groupDraw.groups
        Assertions.assertEquals(5, groups.size)
        Assertions.assertEquals(6, groups[0].matches.size)
        Assertions.assertEquals("A", groups[0].groupName)
        Assertions.assertEquals(6, groups[1].matches.size)
        Assertions.assertEquals("B", groups[1].groupName)
        Assertions.assertEquals(6, groups[2].matches.size)
        Assertions.assertEquals("C", groups[2].groupName)
        Assertions.assertEquals(6, groups[3].matches.size)
        Assertions.assertEquals("D", groups[3].groupName)
        Assertions.assertEquals(6, groups[4].matches.size)
        Assertions.assertEquals("E", groups[4].groupName)

        val playoffMatches = draw.playOff.rounds[0].matches
        val playoffRound = draw.playOff.rounds[0].round

        Assertions.assertEquals(Round.ROUND_OF_16, playoffRound)
        Assertions.assertEquals(playoffMatches.size, 8)
   }

    @Test
    fun testMakeDraw9() {
        val allPlayers = playerRepository.getAll()

        // Get 9 players
        val players = allPlayers.subList(0, 9)
        for (player in players) {
            registrationService.registerPlayerSingles(RegistrationSinglesSpec( player.id, competitionCategoryId))
        }
        val draw = drawService.createDraw(competitionCategoryId)

        // With 9 players there should be 3 groups with 3 matches in each
        val groups = draw.groupDraw.groups
        Assertions.assertEquals(3, groups.size)
        Assertions.assertEquals(3, groups[0].matches.size)
        Assertions.assertEquals("A", groups[0].groupName)
        Assertions.assertEquals(3, groups[1].matches.size)
        Assertions.assertEquals("B", groups[1].groupName)
        Assertions.assertEquals(3, groups[2].matches.size)
        Assertions.assertEquals("C", groups[2].groupName)


        val playoffMatches = draw.playOff.rounds[0].matches
        val playoffRound = draw.playOff.rounds[0].round

        Assertions.assertEquals(Round.QUARTER_FINAL, playoffRound)
        Assertions.assertEquals(playoffMatches.size, 4)
    }

    @Test
    fun testGetFullDraw() {
        val allPlayers = playerRepository.getAll()

        val players = allPlayers.subList(0, 9)
        for (player in players) {
            registrationService.registerPlayerSingles(RegistrationSinglesSpec( player.id, competitionCategoryId))
        }
        drawService.createDraw(competitionCategoryId)

        val draw = drawService.getDraw(competitionCategoryId)
        Assertions.assertNotNull(draw)
        Assertions.assertNotNull(draw.groupDraw.groups)
        Assertions.assertTrue(draw.groupDraw.groups.isNotEmpty())
    }

    @Test
    fun testGetGroupDraw() {
        val allPlayers = playerRepository.getAll()

        val players = allPlayers.subList(0, 9)
        for (player in players) {
            registrationService.registerPlayerSingles(RegistrationSinglesSpec( player.id, competitionCategoryId))
        }
        drawService.createDraw(competitionCategoryId)

        val draw = drawService.getPoolDraw(competitionCategoryId)
        Assertions.assertNotNull(draw)
        Assertions.assertNotNull(draw.groups)
        Assertions.assertTrue(draw.groups.isNotEmpty())
    }

}