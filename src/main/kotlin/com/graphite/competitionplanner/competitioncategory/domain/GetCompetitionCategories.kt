package com.graphite.competitionplanner.competitioncategory.domain

import com.graphite.competitionplanner.competitioncategory.interfaces.CompetitionCategoryDTO
import com.graphite.competitionplanner.competitioncategory.interfaces.ICompetitionCategoryRepository
import org.springframework.stereotype.Component

@Component
class GetCompetitionCategories(
    val repository: ICompetitionCategoryRepository
) {
    fun execute(competitionId: Int): List<CompetitionCategoryDTO> {
        return repository.getAll(competitionId)
    }
}