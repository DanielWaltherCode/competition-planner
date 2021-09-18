package com.graphite.competitionplanner.registration.domain

import com.graphite.competitionplanner.player.interfaces.PlayerDTO
import com.graphite.competitionplanner.registration.interfaces.IRegistrationRepository
import org.springframework.stereotype.Component

@Component
class GetPlayersFromRegistration(
    val repository: IRegistrationRepository
) {

    fun execute(registrationId: Int): List<PlayerDTO> {
        return repository.getPlayersFrom(registrationId)
    }
}