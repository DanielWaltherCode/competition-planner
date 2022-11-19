package com.graphite.competitionplanner.draw.domain

import com.graphite.competitionplanner.competitioncategory.interfaces.CompetitionCategoryDTO
import com.graphite.competitionplanner.competitioncategory.interfaces.CompetitionCategoryStatus
import com.graphite.competitionplanner.competitioncategory.repository.CompetitionCategoryRepository
import com.graphite.competitionplanner.draw.interfaces.ICompetitionDrawRepository

// TODO: Should this potential class be located in another place than Draw.Domain?
class OpenForRegistrations(
    val drawRepository: ICompetitionDrawRepository,
    val competitionCategoryRepository: CompetitionCategoryRepository
) {

    /**
     * This re-opens the competition category for registrations. This will revert any seeding or draw that has has been
     * made.
     *
     * @param competitionCategory CompetitionCategory to re-open
     */
    fun execute(competitionCategory: CompetitionCategoryDTO){
        competitionCategoryRepository.asTransaction {
//            drawRepository.removeSeeding(spec.seeding) // TODO Implement
            drawRepository.delete(competitionCategory.id)
            competitionCategoryRepository.setStatus(
                competitionCategory.id,
                CompetitionCategoryStatus.ACTIVE
            )
        }
    }
}