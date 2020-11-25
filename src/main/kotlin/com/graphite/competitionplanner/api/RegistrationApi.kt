package com.graphite.competitionplanner.api

import com.graphite.competitionplanner.service.*
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/registration")
class RegistrationApi(val registrationService: RegistrationService ) {

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

    @GetMapping("/player/{playerId}")
    fun getByPlayerId(@PathVariable playerId: Int): PlayerCompetitionDTO {
        return registrationService.getRegistrationByPlayerId(playerId)
    }

    @GetMapping("/competition/{competitionId}")
    fun getByCompetitionId(@PathVariable competitionId: Int): CompetitionRegistrationsDTO {
        return registrationService.getRegistrationByCompetition(competitionId)
    }
}

