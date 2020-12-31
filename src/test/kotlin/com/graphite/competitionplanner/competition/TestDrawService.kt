package com.graphite.competitionplanner.competition

import com.graphite.competitionplanner.repositories.PlayerRepository
import com.graphite.competitionplanner.repositories.RegistrationRepository
import com.graphite.competitionplanner.service.RegistrationService
import com.graphite.competitionplanner.service.RegistrationSinglesDTO
import com.graphite.competitionplanner.service.competition.CompetitionCategoryService
import com.graphite.competitionplanner.service.competition.CompetitionDrawService
import com.graphite.competitionplanner.util.TestUtil
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
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
    @Autowired val registrationService: RegistrationService
) {

    var categoryId = 0

    @BeforeEach
    fun addPlayersToCategory() {
        categoryId = testUtil.addCompetitionCategory("Flickor 13")


    }

    @AfterEach
    fun cleanUp() {
        // Remove registrations and delete category
        val registrationIds = registrationRepository.getRegistrationIdsInCategory(categoryId)
        for (id in registrationIds) {
            registrationService.unregister(id)
        }
        competitionCategoryService.deleteCategoryInCompetition(categoryId)
    }

    @Test
    fun testMakeDraw22() {
        val allPlayers = playerRepository.getAll()

        // Get 22 players
        val players = allPlayers.subList(0, 22)
        for (player in players) {
            registrationService.registerPlayerSingles(RegistrationSinglesDTO(null, player.id, categoryId))
        }
        val draw = competitionDrawService.createDraw(categoryId)

        // With 22 players there should be six groups, 4 in the first 4, 3 in the two last ones
        Assertions.assertEquals(6, draw.keys.size)
        Assertions.assertEquals(4, draw[1]?.size)
        Assertions.assertEquals(4, draw[2]?.size)
        Assertions.assertEquals(4, draw[3]?.size)
        Assertions.assertEquals(4, draw[4]?.size)
        Assertions.assertEquals(3, draw[5]?.size)
        Assertions.assertEquals(3, draw[6]?.size)
    }

    @Test
    fun testMakeDraw20() {
        val allPlayers = playerRepository.getAll()

        // Get 20 players
        val players = allPlayers.subList(0, 20)
        for (player in players) {
            registrationService.registerPlayerSingles(RegistrationSinglesDTO(null, player.id, categoryId))
        }
        val draw = competitionDrawService.createDraw(categoryId)

        // With 20 players there should be exactly 5 groups
        Assertions.assertEquals(5, draw.keys.size)
        Assertions.assertEquals(4, draw[1]?.size)
        Assertions.assertEquals(4, draw[2]?.size)
        Assertions.assertEquals(4, draw[3]?.size)
        Assertions.assertEquals(4, draw[4]?.size)
        Assertions.assertEquals(4, draw[5]?.size)
    }

    @Test
    fun testMakeDraw9() {
        val allPlayers = playerRepository.getAll()

        // Get 9 players
        val players = allPlayers.subList(0, 9)
        for (player in players) {
            registrationService.registerPlayerSingles(RegistrationSinglesDTO(null, player.id, categoryId))
        }
        val draw = competitionDrawService.createDraw(categoryId)

        // With 9 players there should be 3 groups with three players in each
        Assertions.assertEquals(3, draw.keys.size)
        Assertions.assertEquals(3, draw[1]?.size)
        Assertions.assertEquals(3, draw[2]?.size)
        Assertions.assertEquals(3, draw[3]?.size)
    }

}