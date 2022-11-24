package com.graphite.competitionplanner.competitioncategory.domain

import com.graphite.competitionplanner.competitioncategory.interfaces.CompetitionCategoryDTO
import com.graphite.competitionplanner.competitioncategory.interfaces.CompetitionCategoryStatus
import com.graphite.competitionplanner.competitioncategory.repository.CompetitionCategoryRepository
import org.springframework.stereotype.Component

@Component
class CloseForRegistrations(
    val findCompetitionCategory: FindCompetitionCategory,
    val competitionCategoryRepository: CompetitionCategoryRepository,
) {

    /**
     * This closes the competition category, preventing any further registrations to the given category
     */
    fun execute(competitionCategory: CompetitionCategoryDTO) {
        competitionCategoryRepository.setStatus(
            competitionCategory.id,
            CompetitionCategoryStatus.CLOSED_FOR_REGISTRATION
        )
    }

}