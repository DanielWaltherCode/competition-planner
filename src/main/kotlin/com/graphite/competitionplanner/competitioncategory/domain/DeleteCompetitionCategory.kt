package com.graphite.competitionplanner.competitioncategory.domain

import com.graphite.competitionplanner.competitioncategory.interfaces.ICompetitionCategoryRepository
import com.graphite.competitionplanner.registration.interfaces.IRegistrationRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.server.ResponseStatusException

@Component
class DeleteCompetitionCategory(
    val competitionCategoryRepository: ICompetitionCategoryRepository,
    val registrationRepository: IRegistrationRepository
) {

    fun execute(competitionCategoryId: Int) {
        val registrations = registrationRepository.getRegistrationsIn(competitionCategoryId)
        if (registrations.isNotEmpty()) {
            throw ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "A category with registered players should not be deleted"
            )
        }
        competitionCategoryRepository.delete(competitionCategoryId)
    }
}