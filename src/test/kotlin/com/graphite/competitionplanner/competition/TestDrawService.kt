package com.graphite.competitionplanner.competition

import com.graphite.competitionplanner.repositories.PlayerRepository
import com.graphite.competitionplanner.repositories.RegistrationRepository
import com.graphite.competitionplanner.service.MatchService
import com.graphite.competitionplanner.service.RegistrationService
import com.graphite.competitionplanner.service.RegistrationSinglesDTO
import com.graphite.competitionplanner.service.competition.CompetitionCategoryService
import com.graphite.competitionplanner.service.competition.CompetitionDrawService
import com.graphite.competitionplanner.util.TestUtil
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestDrawService(
    @Autowired val competitionDrawService: CompetitionDrawService,
    @Autowired val competitionCategoryService: CompetitionCategoryService,
    @Autowired val testUtil: TestUtil,
    @Autowired val playerRepository: PlayerRepository,
    @Autowired val registrationRepository: RegistrationRepository,
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
        competitionDrawService.createDraw(competitionCategoryId)
        val poolDraw = competitionDrawService.getPoolDraw(competitionCategoryId)
        println("Monkey")
        // With 22 players there should be six groups, 4 in the first 4, 3 in the two last ones
    }

    @Test
    fun testMakeDraw20() {
        val allPlayers = playerRepository.getAll()

        // Get 20 players
        val players = allPlayers.subList(0, 20)
        for (player in players) {
            registrationService.registerPlayerSingles(RegistrationSinglesDTO(null, player.id, competitionCategoryId))
        }
        val draw = competitionDrawService.createDraw(competitionCategoryId)

        // With 20 players there should be exactly 5 groups
   }

    @Test
    fun testMakeDraw9() {
        val allPlayers = playerRepository.getAll()

        // Get 9 players
        val players = allPlayers.subList(0, 9)
        for (player in players) {
            registrationService.registerPlayerSingles(RegistrationSinglesDTO(null, player.id, competitionCategoryId))
        }
        val draw = competitionDrawService.createDraw(competitionCategoryId)

        // With 9 players there should be 3 groups with three players in each
    }

}