package com.graphite.competitionplanner.draw.domain

import com.graphite.competitionplanner.competitioncategory.interfaces.ICompetitionCategoryRepository
import com.graphite.competitionplanner.draw.interfaces.ICompetitionDrawRepository
import org.springframework.stereotype.Component

@Component
class DeleteDraw(
    val drawRepository: ICompetitionDrawRepository,
    val competitionCategoryRepository: ICompetitionCategoryRepository
) {

    fun execute(competitionCategoryId: Int) {
        // Validate that competition category exist
        competitionCategoryRepository.get(competitionCategoryId)

        drawRepository.delete(competitionCategoryId)
    }
}