package com.graphite.competitionplanner.api.competition

import com.graphite.competitionplanner.service.*
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
    fun getPlayersInCategory(@PathVariable competitionCategoryId: Int) {

    }

    @PostMapping("/singles")
    fun registerPlayerSingles(registrationSinglesDTO: RegistrationSinglesDTO): Boolean {
        return registrationService.registerPlayerSingles(registrationSinglesDTO)
    }

    @PostMapping("/doubles")
    fun registerPlayerDoubles(registrationDoublesDTO: RegistrationDoublesDTO): Boolean {
        return true
    }

    @DeleteMapping("/{registrationId}")
    fun deleteRegistration(@PathVariable registrationId: Int): Boolean {
        return registrationService.unregister(registrationId)
    }
}