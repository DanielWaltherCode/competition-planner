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
     * @param competitionCategory Competition category to delete.
     * @throws CannotDeleteCompetitionCategoryException If there are registered player in the competition category.
     */
    fun execute(competitionCategory: CompetitionCategoryDTO) {
        val registrations = registrationRepository.getRegistrationsIn(competitionCategory.id)
        if (registrations.isNotEmpty()) {
            throw CannotDeleteCompetitionCategoryException()
        }
        competitionCategoryRepository.delete(competitionCategory.id)
    }
}