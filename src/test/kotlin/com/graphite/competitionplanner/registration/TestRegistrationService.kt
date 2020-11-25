package com.graphite.competitionplanner.registration

import com.graphite.competitionplanner.repositories.CompetitionAndPlayingCategoryRepository
import com.graphite.competitionplanner.repositories.PlayerRepository
import com.graphite.competitionplanner.service.CompetitionService
import com.graphite.competitionplanner.service.PlayerService
import com.graphite.competitionplanner.service.RegistrationService
import com.graphite.competitionplanner.service.RegistrationSinglesDTO
import com.graphite.competitionplanner.util.Util
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestRegistrationService(@Autowired val competitionService: CompetitionService,
                              @Autowired val competitionAndPlayingCategoryRepository: CompetitionAndPlayingCategoryRepository,
                              @Autowired val registrationService: RegistrationService,
                              @Autowired val util: Util,
                              @Autowired val playerService: PlayerService,
@Autowired val playerRepository: PlayerRepository) {

    @Test
    fun getRegistrationsInCompetition() {
        val competitions = competitionService.getCompetitions(null, null)
        val categories = registrationService.getRegistrationByCompetition(competitions[0].id?: 0)
        Assertions.assertNotNull(categories)
        Assertions.assertTrue(categories.registrations.isNotEmpty())
    }

    @Test
    fun getRegistrationsByPlayerId() {
        val playerId = playerRepository.getAll().stream().findFirst().map { it.id }.get()
        val playedCompetitionDTO = registrationService.getRegistrationByPlayerId(playerId)
        Assertions.assertNotNull(playedCompetitionDTO)
        Assertions.assertNotNull(playedCompetitionDTO.player)
        Assertions.assertTrue(playedCompetitionDTO.competitionsAndCategories.isNotEmpty())
    }

    @Test
    fun addRegistration() {
        val lugiId = util.getClubIdOrDefault("Lugi")
        val lugiPlayers = playerService.getPlayersByClubId(lugiId)
        val idToRegister = lugiPlayers[0].id ?: 0

        val originalRegistrations = registrationService.getRegistrationByPlayerId(idToRegister)
        val numberOfRegistrationsInCompetitions = originalRegistrations.competitionsAndCategories.size
        val numberOfRegistrationsInCategories = originalRegistrations.competitionsAndCategories[0].categories.size

        val competitionCategories  = competitionAndPlayingCategoryRepository.getCompetitionCategories()
        registrationService.registerPlayerSingles(RegistrationSinglesDTO(null, idToRegister, competitionCategories[2].id))

    }

}