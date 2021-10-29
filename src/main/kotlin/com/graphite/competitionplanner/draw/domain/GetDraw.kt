package com.graphite.competitionplanner.draw.domain

import com.graphite.competitionplanner.competitioncategory.interfaces.ICompetitionCategoryRepository
import com.graphite.competitionplanner.draw.interfaces.CompetitionCategoryDrawDTO
import com.graphite.competitionplanner.draw.interfaces.ICompetitionDrawRepository
import org.springframework.stereotype.Component

@Component
class GetDraw(
    val drawRepository: ICompetitionDrawRepository,
    val competitionCategoryRepository: ICompetitionCategoryRepository
) {

    fun execute(competitionCategoryId: Int): CompetitionCategoryDrawDTO {
        // Validate that competition category exist
        competitionCategoryRepository.get(competitionCategoryId)

        return drawRepository.get(competitionCategoryId)
    }
}