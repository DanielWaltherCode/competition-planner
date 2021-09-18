package com.graphite.competitionplanner.registration.domain

import com.graphite.competitionplanner.registration.interfaces.IRegistrationRepository
import com.graphite.competitionplanner.registration.interfaces.RegistrationDTO
import org.springframework.stereotype.Component

@Component
class GetRegistrationsInCompetitionCategory(
    val repository: IRegistrationRepository
) {

    fun execute(competitionCategoryId: Int): List<RegistrationDTO> {
        return repository.getRegistrationsIn(competitionCategoryId)
    }
}