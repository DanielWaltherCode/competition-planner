package com.graphite.competitionplanner.api

import com.graphite.competitionplanner.service.RegistrationService
import com.graphite.competitionplanner.tables.pojos.PlayingIn
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/registration")
class RegistrationApi(val registrationService: RegistrationService ) {

    @PostMapping("/singles")
    fun registerPlayerSingles(registrationSinglesDTO: RegistrationSinglesDTO): Boolean {
        val playingIn: PlayingIn = registrationService.registerPlayerSingles(registrationSinglesDTO)
        return (playingIn.id != null)
    }

    @PostMapping("/doubles")
    fun registerPlayerDoubles(registrationDoublesDTO: RegistrationDoublesDTO): Boolean {
        return true
    }
}

data class RegistrationSinglesDTO(
        val id: Int?,
        val playerId: Int,
        val competitionPlayingCategoryId: Int
)

data class RegistrationDoublesDTO(
        val id: Int?,
        val firstPlayerId: Int,
        val secondPlayerId: Int,
        val competitionPlayingCategoryId: Int
)