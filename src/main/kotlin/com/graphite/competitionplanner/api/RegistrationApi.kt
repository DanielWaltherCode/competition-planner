package com.graphite.competitionplanner.api

import com.graphite.competitionplanner.service.RegistrationDoublesDTO
import com.graphite.competitionplanner.service.RegistrationService
import com.graphite.competitionplanner.service.RegistrationSinglesDTO
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

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
}

