package com.graphite.competitionplanner.service

import com.graphite.competitionplanner.api.RegistrationSinglesDTO
import com.graphite.competitionplanner.repositories.RegistrationRepository
import com.graphite.competitionplanner.tables.pojos.PlayerRegistration
import com.graphite.competitionplanner.tables.pojos.PlayingIn
import com.graphite.competitionplanner.tables.pojos.Registration
import org.springframework.stereotype.Service
import java.time.LocalDate
import kotlin.random.Random

@Service
class RegistrationService(val registrationRepository: RegistrationRepository) {

    fun registerPlayerSingles(registrationSinglesDTO: RegistrationSinglesDTO): PlayingIn {
        val registration: Registration = registrationRepository.addRegistration(LocalDate.now())
        val playerRegistration: PlayerRegistration = registrationRepository.registerPlayer(registration.id, registrationSinglesDTO.playerId)
        return registrationRepository.registerPlayingIn(registration.id, null, registrationSinglesDTO.competitionPlayingCategoryId)
    }

    fun getSeed(): Int {
        return Random.nextInt(0, 16)
    }
}