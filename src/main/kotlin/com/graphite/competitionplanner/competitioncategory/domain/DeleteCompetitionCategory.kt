package com.graphite.competitionplanner.competitioncategory.domain

import com.graphite.competitionplanner.competitioncategory.interfaces.CompetitionCategoryDTO
import com.graphite.competitionplanner.competitioncategory.interfaces.ICompetitionCategoryRepository
import com.graphite.competitionplanner.registration.interfaces.IRegistrationRepository
import org.springframework.stereotype.Component

@Component
class DeleteCompetitionCategory(
    val competitionCategoryRepository: ICompetitionCategoryRepository,
    val registrationRepository: IRegistrationRepository
) {

    /**
     * Delete the competition category.
     *
     * @param competitionCategoryId Competition category to delete.
     * @throws CannotDeleteCompetitionCategoryException If there are registered player in the competition category.
     */
    fun execute(competitionCategoryId: Int) {
        val registrations = registrationRepository.getRegistrationsIn(competitionCategoryId)
        if (registrations.isNotEmpty()) {
            throw CannotDeleteCompetitionCategoryException()
        }
        competitionCategoryRepository.delete(competitionCategoryId)
    }
}