package com.graphite.competitionplanner.draw.domain

import com.graphite.competitionplanner.competitioncategory.domain.CloseForRegistrations
import com.graphite.competitionplanner.competitioncategory.interfaces.ICompetitionCategoryRepository
import com.graphite.competitionplanner.draw.interfaces.ICompetitionDrawRepository
import org.springframework.stereotype.Component

@Component
class DeleteDraw(
    val drawRepository: ICompetitionDrawRepository,
    val competitionCategoryRepository: ICompetitionCategoryRepository,
    val closeForRegistrations: CloseForRegistrations
) {

    /**
     * Delete the draw of the given CompetitionCategory.
     *
     * When a draw of a CompetitionCategory is deleted, the seeding is kept and the CompetitionCategory is closed
     * for registration.
     *
     * @param competitionCategoryId ID of the competition category
     */
    fun execute(competitionCategoryId: Int) {
        // Validate that competition category exist
        val competitionCategory = competitionCategoryRepository.get(competitionCategoryId)

        drawRepository.asTransaction {
            drawRepository.delete(competitionCategoryId)
            closeForRegistrations.execute(competitionCategory)
        }
    }
}