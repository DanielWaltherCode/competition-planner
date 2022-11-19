package com.graphite.competitionplanner.draw.domain

import com.graphite.competitionplanner.competitioncategory.interfaces.CompetitionCategoryDTO
import com.graphite.competitionplanner.competitioncategory.interfaces.CompetitionCategoryStatus
import com.graphite.competitionplanner.competitioncategory.repository.CompetitionCategoryRepository
import com.graphite.competitionplanner.draw.interfaces.ApproveSeedingSpec
import com.graphite.competitionplanner.draw.interfaces.ICompetitionDrawRepository
import org.springframework.stereotype.Component

@Component
class ApproveSeeding(
    val drawRepository: ICompetitionDrawRepository,
    val competitionCategoryRepository: CompetitionCategoryRepository
) {

    /**
     * Approve and commit the given seeding. When a seeding is approved, the competition category will be closed
     * for further registrations as that would affect the seeding.
     */
    fun execute(competitionCategory: CompetitionCategoryDTO, spec: ApproveSeedingSpec) {
        competitionCategoryRepository.asTransaction {
            drawRepository.storeSeeding(spec.seeding)
            competitionCategoryRepository.setStatus(
                competitionCategory.id,
                CompetitionCategoryStatus.CLOSED_FOR_REGISTRATION
            )
        }
    }
}