package com.graphite.competitionplanner.service

import com.graphite.competitionplanner.repositories.RegistrationRepository
import org.springframework.stereotype.Service
import java.time.LocalDate
import kotlin.random.Random

@Service
class RegistrationService(val registrationRepository: RegistrationRepository) {

    fun registerPlayerSingles(registrationSinglesDTO: RegistrationSinglesDTO): Boolean {
        val registration = registrationRepository.addRegistration(LocalDate.now())
        registrationRepository.registerPlayer(registration.id, registrationSinglesDTO.playerId)
        val playingInRecord = registrationRepository.registerPlayingIn(registration.id, null, registrationSinglesDTO.competitionPlayingCategoryId)
        return playingInRecord.id != null
    }

    fun getSeed(): Int {
        return Random.nextInt(0, 16)
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