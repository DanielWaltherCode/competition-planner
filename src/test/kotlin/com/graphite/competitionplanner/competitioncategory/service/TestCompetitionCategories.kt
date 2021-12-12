package com.graphite.competitionplanner.competitioncategory.service

import com.graphite.competitionplanner.competition.domain.FindCompetitions
import com.graphite.competitionplanner.competitioncategory.domain.DeleteCompetitionCategory
import com.graphite.competitionplanner.competitioncategory.domain.GetCompetitionCategories
import com.graphite.competitionplanner.competitioncategory.repository.CompetitionCategoryRepository
import com.graphite.competitionplanner.player.domain.ListAllPlayersInClub
import com.graphite.competitionplanner.registration.interfaces.RegistrationSinglesSpec
import com.graphite.competitionplanner.registration.service.RegistrationService
import com.graphite.competitionplanner.util.TestUtil
import com.graphite.competitionplanner.util.Util
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.web.server.ResponseStatusException

@SpringBootTest
class TestCompetitionCategories(
    @Autowired val util: Util,
    @Autowired val listAllPlayersInClub: ListAllPlayersInClub,
    @Autowired val registrationService: RegistrationService,
    @Autowired val competitionCategoryRepository: CompetitionCategoryRepository,
    @Autowired val testUtil: TestUtil,
    @Autowired val findCompetitions: FindCompetitions,
    @Autowired val getCompetitionCategories: GetCompetitionCategories,
    @Autowired val deleteCompetitionCategory: DeleteCompetitionCategory,
) {

    @Test
    fun getCategoriesInCompetition() {
        // Adds one category to Umeå
        testUtil.addCompetitionCategory("Damer 4")

        val umeaId = util.getClubIdOrDefault("Umeå IK")
        val competitions = findCompetitions.thatBelongsTo(umeaId)
        val competitionCategories = getCompetitionCategories.execute(competitions[0].id)
        Assertions.assertTrue(competitionCategories.isNotEmpty())
    }

    @Test
    fun deleteCompetitionCategory() {
        val originalLength = competitionCategoryRepository.getCompetitionCategories().size
        val newCategoryId = testUtil.addCompetitionCategory("Herrar 3")

        // No players registered in competition, deleting should be fine
        deleteCompetitionCategory.execute(newCategoryId)

        Assertions.assertEquals(originalLength, competitionCategoryRepository.getCompetitionCategories().size )
    }

    @Test
    fun deleteCompetitionCategoryAfterRegistration() {
        val originalLength = competitionCategoryRepository.getCompetitionCategories().size
        val newCategoryId = testUtil.addCompetitionCategory("Herrar 4")

        // Register players
        val umePlayers = listAllPlayersInClub.execute(util.getClubIdOrDefault("Umeå IK"))
        registrationService.registerPlayerSingles(RegistrationSinglesSpec(umePlayers[0].id, newCategoryId))

        // Try deleting after players have registered, should fail
        Assertions.assertThrows(ResponseStatusException::class.java) {
            deleteCompetitionCategory.execute(newCategoryId)
        }

        // Assert nothing has been removed
        val newLength = competitionCategoryRepository.getCompetitionCategories().size
        Assertions.assertEquals(originalLength + 1, newLength)
    }

}