package com.graphite.competitionplanner.competition

import com.graphite.competitionplanner.repositories.competition.CompetitionCategoryRepository
import com.graphite.competitionplanner.repositories.competition.CategoryRepository
import com.graphite.competitionplanner.service.competition.CompetitionService
import com.graphite.competitionplanner.service.PlayerService
import com.graphite.competitionplanner.service.RegistrationService
import com.graphite.competitionplanner.service.RegistrationSinglesDTO
import com.graphite.competitionplanner.service.competition.CompetitionCategoryService
import com.graphite.competitionplanner.util.TestUtil
import com.graphite.competitionplanner.util.Util
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.web.server.ResponseStatusException

@SpringBootTest
class TestCompetitionCategories(
    @Autowired val competitionService: CompetitionService,
    @Autowired val util: Util,
    @Autowired val playerService: PlayerService,
    @Autowired val registrationService: RegistrationService,
    @Autowired val competitionCategoryRepository: CompetitionCategoryRepository,
    @Autowired val competitionCategoryService: CompetitionCategoryService,
    @Autowired val testUtil: TestUtil
) {

    @Test
    fun getCategoriesInCompetition() {
        // Adds one category to Umeå
        testUtil.addCompetitionCategory("Damer 4")

        val umeaId = util.getClubIdOrDefault("Umeå IK")
        val competitions = competitionService.getByClubId(umeaId)
        val competitionCategories = competitionService.getCategoriesInCompetition(competitions[0].id ?: 0)
        Assertions.assertTrue(competitionCategories.categories.isNotEmpty())
        Assertions.assertNotNull(competitionCategories.competition.organizingClub)
    }

    @Test
    fun deleteCompetitionCategory() {
        val originalLength = competitionCategoryRepository.getCompetitionCategories().size
        val newCategoryId = testUtil.addCompetitionCategory("Herrar 3")

        // No players registered in competition, deleting should be fine
        competitionCategoryService.deleteCategoryInCompetition(newCategoryId)

        Assertions.assertEquals(originalLength, competitionCategoryRepository.getCompetitionCategories().size )
    }

    @Test
    fun deleteCompetitionCategoryAfterRegistration() {
        val originalLength = competitionCategoryRepository.getCompetitionCategories().size
        val newCategoryId = testUtil.addCompetitionCategory("Herrar 4")

        // Register players
        val umePlayers = playerService.getPlayersByClubId(util.getClubIdOrDefault("Umeå IK"))
        registrationService.registerPlayerSingles( RegistrationSinglesDTO(null, umePlayers[0].id ?: 0, newCategoryId))

        // Try deleting after players have registered, should fail
        Assertions.assertThrows(ResponseStatusException::class.java) {
            competitionCategoryService.deleteCategoryInCompetition(newCategoryId)
        }

        // Assert nothing has been removed
        val newLength = competitionCategoryRepository.getCompetitionCategories().size
        Assertions.assertEquals(originalLength + 1, newLength)
    }

    @Test
    fun cancelCompetitionCategory() {
        val newCategoryId = testUtil.addCompetitionCategory("Herrar 5")

        competitionCategoryService.cancelCategoryInCompetition(newCategoryId)

        val competitionCategory = competitionCategoryRepository.getById(newCategoryId)
        Assertions.assertEquals("CANCELLED", competitionCategory.status)

    }


}