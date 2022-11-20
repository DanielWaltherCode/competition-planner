package com.graphite.competitionplanner.draw.domain

import com.graphite.competitionplanner.competitioncategory.interfaces.CompetitionCategoryDTO
import com.graphite.competitionplanner.competitioncategory.interfaces.CompetitionCategoryStatus
import com.graphite.competitionplanner.competitioncategory.repository.CompetitionCategoryRepository
import com.graphite.competitionplanner.draw.interfaces.ICompetitionDrawRepository

class OpenForRegistrations(
    val drawRepository: ICompetitionDrawRepository,
    val competitionCategoryRepository: CompetitionCategoryRepository
) {

    /**
     * This re-opens the competition category for registrations. This will revert any seeding and draw that has been
     * made.
     *
     * @param competitionCategory CompetitionCategory to re-open
     */
    fun execute(competitionCategory: CompetitionCategoryDTO){
        competitionCategoryRepository.asTransaction {
            drawRepository.delete(competitionCategory.id)
            drawRepository.deleteSeeding(competitionCategory.id)
            competitionCategoryRepository.setStatus(
                competitionCategory.id,
                CompetitionCategoryStatus.OPEN_FOR_REGISTRATION
            )
        }
    }
}