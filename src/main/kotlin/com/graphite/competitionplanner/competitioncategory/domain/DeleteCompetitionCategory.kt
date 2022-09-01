package com.graphite.competitionplanner.competitioncategory.domain

import com.graphite.competitionplanner.common.exception.BadRequestException
import com.graphite.competitionplanner.common.exception.BadRequestType
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
     * @throws BadRequestException If there are registered player in the competition category.
     */
    fun execute(competitionCategoryId: Int) {
        val registrations = registrationRepository.getRegistrationsIn(competitionCategoryId)
        if (registrations.isNotEmpty()) {
            throw BadRequestException(BadRequestType.CATEGORY_CANNOT_DELETE_WITH_PLAYERS, "Cannot delete a category that has registered players in it")
        }
        competitionCategoryRepository.delete(competitionCategoryId)
    }
}