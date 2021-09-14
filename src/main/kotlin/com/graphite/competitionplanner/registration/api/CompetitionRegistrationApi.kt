package com.graphite.competitionplanner.registration.api

import com.graphite.competitionplanner.competition.service.CompetitionService
import com.graphite.competitionplanner.player.interfaces.PlayerDTO
import com.graphite.competitionplanner.registration.interfaces.RegistrationSinglesSpec
import com.graphite.competitionplanner.registration.service.RegisteredPlayersDTO
import com.graphite.competitionplanner.registration.service.RegistrationDoublesDTO
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
    fun getPlayersInCategory(@PathVariable competitionCategoryId: Int): List<List<PlayerDTO>> {
        return registrationService.getPlayersInCompetitionCategory(competitionCategoryId)
    }

    @PostMapping("/singles")
    fun registerPlayerSingles(@RequestBody registrationSinglesSpec: RegistrationSinglesSpec): Int {
        // Returns registration if registration successful, -1 otherwise
        return registrationService.registerPlayerSingles(registrationSinglesSpec)
    }

    @PostMapping("/doubles")
    fun registerPlayerDoubles(registrationDoublesDTO: RegistrationDoublesDTO): Boolean {
        // TODO:  Implement
        return true
    }

    @DeleteMapping("/{registrationId}")
    fun deleteRegistration(@PathVariable registrationId: Int): Boolean {
        return registrationService.unregister(registrationId)
    }
}

