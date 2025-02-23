package com.graphite.competitionplanner.registration.api

import com.graphite.competitionplanner.player.interfaces.PlayerWithClubDTO
import com.graphite.competitionplanner.registration.domain.SearchRegistrations
import com.graphite.competitionplanner.registration.domain.Withdraw
import com.graphite.competitionplanner.registration.interfaces.*
import com.graphite.competitionplanner.registration.repository.RegistrationRepository
import com.graphite.competitionplanner.registration.service.RegisteredPlayersDTO
import com.graphite.competitionplanner.registration.service.RegistrationService
import io.swagger.v3.oas.annotations.Operation
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("api/competition/{competitionId}/registration")
class CompetitionRegistrationApi(
        val registrationService: RegistrationService,
        val registrationRepository: RegistrationRepository,
        val withDraw: Withdraw,
        val searchRegistrations: SearchRegistrations
) {

    // Supports search by club, category, name
    @GetMapping
    @Operation(summary = "Allowed values: club, category, name")
    fun getPlayersInCompetition(
        @PathVariable competitionId: Int,
        @RequestParam searchType: String
    ): RegisteredPlayersDTO {
        return registrationService.getRegisteredPlayers(competitionId, searchType)
    }

    // Only called before draw is made
    @GetMapping("/{competitionCategoryId}/singles")
    fun getPlayersInCategorySingles(@PathVariable competitionCategoryId: Int): RegisteredPlayersDTO {
        return searchRegistrations.getResultGroupedByLastNameForCategory(competitionCategoryId)
    }

    // Only called before draw is made
    @GetMapping("/{competitionCategoryId}/doubles")
    fun getPlayersInCategoryDoubles(@PathVariable competitionCategoryId: Int): List<List<PlayerWithClubDTO>> {
        return registrationService.getPlayersInCompetitionCategory(competitionCategoryId)
    }

    @PostMapping("/singles")
    fun registerPlayerSingles(@RequestBody registrationSinglesSpec: RegistrationSinglesSpec): RegistrationSinglesDTO {
        return registrationService.registerPlayerSingles(registrationSinglesSpec)
    }

    @PostMapping("/doubles")
    fun registerPlayerDoubles(@RequestBody spec: RegistrationDoublesSpec): RegistrationDoublesDTO {
        return registrationService.registerPlayersDoubles(spec)
    }

    @DeleteMapping("/{registrationId}")
    fun deleteRegistration(@PathVariable registrationId: Int) {
        registrationService.unregister(registrationId)
    }

    // Retrieves player information and all the categories and player is registered in
    // in a given competition (including who they are playing with in doubles)
    @GetMapping("/player/{playerId}")
    fun getRegistrationsForPlayer(@PathVariable competitionId: Int,
                                  @PathVariable playerId: Int): PlayerRegistrationDTO {
        return registrationService.getRegistrationsForPlayerInCompetition(competitionId, playerId)

    }

    // Return the registration id for a player in a given category
    @GetMapping("/player/{competitionCategoryId}/{playerId}")
    fun getRegistrationId(@PathVariable competitionId: Int,
                                  @PathVariable competitionCategoryId: Int,
                                  @PathVariable playerId: Int): Int {
        return registrationRepository.getRegistrationIdForPlayerInCategory(competitionCategoryId, playerId)

    }

    @PutMapping("/withdraw/{competitionCategoryId}/{registrationId}")
    fun withdrawFromCategory(@PathVariable competitionId: Int,
                             @PathVariable registrationId: Int,
                             @PathVariable competitionCategoryId: Int) {
        withDraw.beforeCompetition(competitionId, competitionCategoryId, registrationId)
    }

    @PutMapping("/walkover/{competitionCategoryId}/{registrationId}")
    fun reportWalkover(@PathVariable competitionId: Int,
                       @PathVariable registrationId: Int,
                       @PathVariable competitionCategoryId: Int) {
            withDraw.walkOver(competitionId, competitionCategoryId, registrationId)
        }
}

