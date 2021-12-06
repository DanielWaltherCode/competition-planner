package com.graphite.competitionplanner.draw.service

import com.graphite.competitionplanner.competitioncategory.service.CompetitionCategoryService
import com.graphite.competitionplanner.competitioncategory.entity.Round
import com.graphite.competitionplanner.draw.repository.CompetitionDrawRepository
import com.graphite.competitionplanner.match.service.MatchService
import com.graphite.competitionplanner.player.repository.PlayerRepository
import com.graphite.competitionplanner.registration.interfaces.RegistrationSinglesSpec
import com.graphite.competitionplanner.registration.repository.RegistrationRepository
import com.graphite.competitionplanner.registration.service.RegistrationService
import com.graphite.competitionplanner.util.DataGenerator
import com.graphite.competitionplanner.util.TestUtil
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestCreateGroupDrawOneProceed(@Autowired val testUtil: TestUtil,
                                    @Autowired val registrationRepository: RegistrationRepository,
                                    @Autowired val registrationService: RegistrationService,
                                    @Autowired val matchService: MatchService,
                                    @Autowired val playerRepository: PlayerRepository,
                                    @Autowired val competitionDrawRepository: CompetitionDrawRepository,
                                    @Autowired val drawService: DrawService,
                                    @Autowired val competitionCategoryService: CompetitionCategoryService
) {
    var competitionCategoryId = 0
    val dataGenerator = DataGenerator()

    @BeforeEach
    fun addPlayersToCategory() {
        competitionCategoryId = testUtil.addCompetitionCategory("Flickor 13")

        // Update competition category so that it's groups of 3 instead with one proceeding
        val original = competitionCategoryService.getByCompetitionCategoryId(competitionCategoryId)

        val updatedSettings = dataGenerator.newCompetitionCategoryUpdateSpec(
            settings = dataGenerator.newGeneralSettingsSpec(
                cost = original.settings.cost,
                drawType = original.settings.drawType,
                playersPerGroup = 3,
                playersToPlayOff = 1,
                poolDrawStrategy = original.settings.poolDrawStrategy
            ),
            gameSettings = dataGenerator.newGameSettingsSpec(
                original.gameSettings.numberOfSets,
                original.gameSettings.winScore,
                original.gameSettings.winMargin,
                original.gameSettings.differentNumberOfGamesFromRound,
                original.gameSettings.numberOfSetsFinal,
                original.gameSettings.winScoreFinal,
                original.gameSettings.winMarginFinal,
                original.gameSettings.tiebreakInFinalGame,
                original.gameSettings.winScoreTiebreak,
                original.gameSettings.winMarginTieBreak
            )
        )

        competitionCategoryService.updateCompetitionCategory(original.id, updatedSettings)
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
            registrationService.registerPlayerSingles(RegistrationSinglesSpec(player.id, competitionCategoryId))
        }
        val draw = drawService.createDraw(competitionCategoryId)
        // With 22 players there should be 7 groups, 3 matches in the first 6, 6 in the last one
        Assertions.assertNotNull(draw)
        val groups = draw.groupDraw.groups
        Assertions.assertEquals(7, groups.size)
        Assertions.assertEquals(6, groups[0].matches.size)
        Assertions.assertEquals("A", groups[0].groupName)
        Assertions.assertEquals(3, groups[1].matches.size)
        Assertions.assertEquals("B", groups[1].groupName)
        Assertions.assertEquals(3, groups[2].matches.size)
        Assertions.assertEquals("C", groups[2].groupName)
        Assertions.assertEquals(3, groups[3].matches.size)
        Assertions.assertEquals("D", groups[3].groupName)
        Assertions.assertEquals(3, groups[4].matches.size)
        Assertions.assertEquals("E", groups[4].groupName)
        Assertions.assertEquals(3, groups[5].matches.size)
        Assertions.assertEquals("F", groups[5].groupName)
        Assertions.assertEquals(3, groups[6].matches.size)
        Assertions.assertEquals("G", groups[6].groupName)


        val playoffMatches = draw.playOff.rounds[0].matches
        val playoffRound = draw.playOff.rounds[0].round

        Assertions.assertEquals(Round.QUARTER_FINAL, playoffRound)
        Assertions.assertEquals(playoffMatches.size, 4)
    }

    @Test
    fun testMakeDraw20() {
        val allPlayers = playerRepository.getAll()

        // Get 21 players
        val players = allPlayers.subList(0, 21)
        for (player in players) {
            registrationService.registerPlayerSingles(RegistrationSinglesSpec(player.id, competitionCategoryId))
        }
        val draw = drawService.createDraw(competitionCategoryId)

        // With 21 players there should be exactly 7 groups
        val groups = draw.groupDraw.groups
        Assertions.assertEquals(7, groups.size)
        Assertions.assertEquals(3, groups[0].matches.size)
        Assertions.assertEquals("A", groups[0].groupName)
        Assertions.assertEquals(3, groups[1].matches.size)
        Assertions.assertEquals("B", groups[1].groupName)
        Assertions.assertEquals(3, groups[2].matches.size)
        Assertions.assertEquals("C", groups[2].groupName)
        Assertions.assertEquals(3, groups[3].matches.size)
        Assertions.assertEquals("D", groups[3].groupName)
        Assertions.assertEquals(3, groups[4].matches.size)
        Assertions.assertEquals("E", groups[4].groupName)
        Assertions.assertEquals(3, groups[5].matches.size)
        Assertions.assertEquals("F", groups[5].groupName)
        Assertions.assertEquals(3, groups[6].matches.size)
        Assertions.assertEquals("G", groups[6].groupName)

        val playoffMatches = draw.playOff.rounds[0].matches
        val playoffRound = draw.playOff.rounds[0].round

        Assertions.assertEquals(Round.QUARTER_FINAL, playoffRound)
        Assertions.assertEquals(playoffMatches.size, 4)
    }

    @Test
    fun testMakeDraw14() {
        val allPlayers = playerRepository.getAll()

        // Get 14 players
        val players = allPlayers.subList(0, 14)
        for (player in players) {
            registrationService.registerPlayerSingles(RegistrationSinglesSpec(player.id, competitionCategoryId))
        }
        val draw = drawService.createDraw(competitionCategoryId)

        // With 14 players there should be 4 groups, 6 matches in first two
        val groups = draw.groupDraw.groups
        Assertions.assertEquals(4, groups.size)
        Assertions.assertEquals(6, groups[0].matches.size)
        Assertions.assertEquals("A", groups[0].groupName)
        Assertions.assertEquals(6, groups[1].matches.size)
        Assertions.assertEquals("B", groups[1].groupName)
        Assertions.assertEquals(3, groups[2].matches.size)
        Assertions.assertEquals("C", groups[2].groupName)
        Assertions.assertEquals(3, groups[3].matches.size)
        Assertions.assertEquals("D", groups[3].groupName)

        val playoffMatches = draw.playOff.rounds[0].matches
        val playoffRound = draw.playOff.rounds[0].round

        Assertions.assertEquals(Round.SEMI_FINAL, playoffRound)
        Assertions.assertEquals(playoffMatches.size, 2)
    }
}