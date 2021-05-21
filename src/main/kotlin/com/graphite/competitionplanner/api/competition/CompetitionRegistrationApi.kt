package com.graphite.competitionplanner.api.competition

import com.graphite.competitionplanner.service.*
import com.graphite.competitionplanner.service.competition.CompetitionService
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
    fun registerPlayerSingles(@RequestBody registrationSinglesSpec: RegistrationSinglesSpec): Boolean {
        return registrationService.registerPlayerSingles(registrationSinglesSpec)
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

data class RegistrationSinglesSpec(
    val playerId: Int,
    val competitionCategoryId: Int
)

data class RegistrationDoublesSpec(
    val firstPlayerId: Int,
    val secondPlayerId: Int,
    val competitionCategoryId: Int
)