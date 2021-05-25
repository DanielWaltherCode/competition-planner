package com.graphite.competitionplanner.registration

import com.graphite.competitionplanner.api.competition.RegistrationSinglesSpec
import com.graphite.competitionplanner.repositories.PlayerRepository
import com.graphite.competitionplanner.repositories.competition.CompetitionCategoryRepository
import com.graphite.competitionplanner.service.competition.CompetitionService
import com.graphite.competitionplanner.service.PlayerService
import com.graphite.competitionplanner.service.RegistrationService
import com.graphite.competitionplanner.service.RegistrationSinglesDTO
import com.graphite.competitionplanner.util.Util
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

// TODO -- Rewrite class to set up and remove data before and after each test
@SpringBootTest
class TestRegistrationService(@Autowired val competitionService: CompetitionService,
                              @Autowired val competitionCategoryRepository: CompetitionCategoryRepository,
                              @Autowired val registrationService: RegistrationService,
                              @Autowired val util: Util,
                              @Autowired val playerService: PlayerService,
                              @Autowired val playerRepository: PlayerRepository) {

    @Test
    fun getRegistrationsInCompetition() {
        val competitions = competitionService.getCompetitions(null, null)
    }

    @Test
    @Disabled
    fun getRegistrationsByPlayerId() {
        val playerId = playerRepository.getAll().stream().findFirst().map { it.id }.get()
        val playedCompetitionDTO = registrationService.getRegistrationByPlayerId(playerId)
        Assertions.assertNotNull(playedCompetitionDTO)
        Assertions.assertNotNull(playedCompetitionDTO.player)
        Assertions.assertTrue(playedCompetitionDTO.competitionsAndCategories.isNotEmpty())
    }

    @Test
    @Disabled
    fun addRegistration() {
        val umeId = util.getClubIdOrDefault("Umeå IK")
        val umePlayers = playerService.getPlayersByClubId(umeId)
        val idToRegister = umePlayers[3].id

        val originalRegistrations = registrationService.getRegistrationByPlayerId(idToRegister)
        val numberOfRegistrations = originalRegistrations.competitionsAndCategories.size

        val competitions = competitionService.getByClubId(umeId)
        val categoriesInCompetitionOne = competitionCategoryRepository.getCategoriesInCompetition(competitions[0].id?: 0)
        // Register in same competition different category
        registrationService.registerPlayerSingles(RegistrationSinglesSpec( idToRegister, categoriesInCompetitionOne[2].categoryId))

        val newRegistrations = registrationService.getRegistrationByPlayerId(idToRegister)
        val newNumberOfRegistrations = newRegistrations.competitionsAndCategories.size

        Assertions.assertEquals(numberOfRegistrations + 1, newNumberOfRegistrations)

    }

}