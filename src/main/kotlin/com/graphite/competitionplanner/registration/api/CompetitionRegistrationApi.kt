package com.graphite.competitionplanner.registration.api

import com.graphite.competitionplanner.competition.service.CompetitionService
import com.graphite.competitionplanner.player.interfaces.PlayerWithClubDTO
import com.graphite.competitionplanner.registration.interfaces.*
import com.graphite.competitionplanner.registration.service.RegisteredPlayersDTO
import com.graphite.competitionplanner.registration.service.RegistrationService
import io.swagger.annotations.ApiModelProperty
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/competition/{competitionId}/registration")
class CompetitionRegistrationApi(
    val competitionService: CompetitionService,
    val registrationService: RegistrationService
) {

    // Supports search by club, category, name
    @GetMapping
    @ApiModelProperty(value = "Allowed values", allowableValues = "club, category, name", required = false)
    fun getPlayersInCompetition(
        @PathVariable competitionId: Int,
        @RequestParam searchType: String
    ): RegisteredPlayersDTO {
        return registrationService.getRegisteredPlayers(competitionId, searchType)
    }

    @GetMapping("/{competitionCategoryId}")
    fun getPlayersInCategory(@PathVariable competitionCategoryId: Int): List<List<PlayerWithClubDTO>> {
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
}

