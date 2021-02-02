package com.graphite.competitionplanner.draw

import com.graphite.competitionplanner.api.competition.CategoryMetadataSpec
import com.graphite.competitionplanner.repositories.PlayerRepository
import com.graphite.competitionplanner.repositories.RegistrationRepository
import com.graphite.competitionplanner.service.CategoryService
import com.graphite.competitionplanner.service.MatchService
import com.graphite.competitionplanner.service.RegistrationService
import com.graphite.competitionplanner.service.RegistrationSinglesDTO
import com.graphite.competitionplanner.service.competition.CompetitionCategoryService
import com.graphite.competitionplanner.service.competition.DrawService
import com.graphite.competitionplanner.service.competition.Round
import com.graphite.competitionplanner.util.TestUtil
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDateTime

@SpringBootTest
class TestCreateGroupDrawOneProceed(@Autowired val testUtil: TestUtil,
                                    @Autowired val registrationRepository: RegistrationRepository,
                                    @Autowired val registrationService: RegistrationService,
                                    @Autowired val matchService: MatchService,
                                    @Autowired val playerRepository: PlayerRepository,
                                    @Autowired val drawService: DrawService,
                                    @Autowired val categoryService: CategoryService,
                                    @Autowired val competitionCategoryService: CompetitionCategoryService
) {
    var competitionCategoryId = 0

    @BeforeEach
    fun addPlayersToCategory() {
        competitionCategoryId = testUtil.addCompetitionCategory("Flickor 13")

        // Update competition category so that it's groups of 3 instead with one proceeding
        val categoryMetadata = categoryService.getCategoryMetadata(competitionCategoryId)
        val categoryMetadataSpec = CategoryMetadataSpec(
            cost = categoryMetadata.cost,
            startTime = categoryMetadata.startTime,
            drawTypeId = categoryMetadata.drawType.id,
            nrPlayersPerGroup = 3,
            nrPlayersToPlayoff = 1,
            poolDrawStrategyId = categoryMetadata.poolDrawStrategyId
        )

        categoryService.updateCategoryMetadata(competitionCategoryId, categoryMetadata.id, categoryMetadataSpec)
    }

    @AfterEach
    fun cleanUp() {
        // Remove matches
        matchService.deleteMatchesInCategory(competitionCategoryId)

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
            registrationService.registerPlayerSingles(RegistrationSinglesDTO(null, player.id, competitionCategoryId))
        }
        val draw = drawService.createDraw(competitionCategoryId)
        // With 22 players there should be 7 groups, 3 matches in the first 6, 6 in the last one
        Assertions.assertNotNull(draw)
        val groups = draw.groupDraw.groups
        Assertions.assertEquals(7, groups.size)
        Assertions.assertEquals(3, groups["A"]?.size)
        Assertions.assertEquals(3, groups["B"]?.size)
        Assertions.assertEquals(3, groups["C"]?.size)
        Assertions.assertEquals(3, groups["D"]?.size)
        Assertions.assertEquals(3, groups["E"]?.size)
        Assertions.assertEquals(3, groups["F"]?.size)
        Assertions.assertEquals(6, groups["G"]?.size)

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
            registrationService.registerPlayerSingles(RegistrationSinglesDTO(null, player.id, competitionCategoryId))
        }
        val draw = drawService.createDraw(competitionCategoryId)

        // With 21 players there should be exactly 7 groups
        val groups = draw.groupDraw.groups
        Assertions.assertEquals(7, groups.size)
        Assertions.assertEquals(3, groups["A"]?.size)
        Assertions.assertEquals(3, groups["B"]?.size)
        Assertions.assertEquals(3, groups["C"]?.size)
        Assertions.assertEquals(3, groups["D"]?.size)
        Assertions.assertEquals(3, groups["E"]?.size)
        Assertions.assertEquals(3, groups["F"]?.size)
        Assertions.assertEquals(3, groups["G"]?.size)

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
            registrationService.registerPlayerSingles(RegistrationSinglesDTO(null, player.id, competitionCategoryId))
        }
        val draw = drawService.createDraw(competitionCategoryId)

        // With 14 players there should be 4 groups, 6 matches in two last ones
        val groups = draw.groupDraw.groups
        Assertions.assertEquals(4, groups.size)
        Assertions.assertEquals(3, groups["A"]?.size)
        Assertions.assertEquals(3, groups["B"]?.size)
        Assertions.assertEquals(6, groups["C"]?.size)
        Assertions.assertEquals(6, groups["D"]?.size)

        val playoffMatches = draw.playOff.rounds[0].matches
        val playoffRound = draw.playOff.rounds[0].round

        Assertions.assertEquals(Round.SEMI_FINAL, playoffRound)
        Assertions.assertEquals(playoffMatches.size, 2)
    }
}