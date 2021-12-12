package com.graphite.competitionplanner.competitioncategory.domain

import com.graphite.competitionplanner.competitioncategory.interfaces.CompetitionCategoryDTO
import com.graphite.competitionplanner.competitioncategory.interfaces.CompetitionCategoryStatus
import com.graphite.competitionplanner.competitioncategory.interfaces.ICompetitionCategoryRepository
import org.springframework.stereotype.Component

@Component
class CancelCompetitionCategory(
    val repository: ICompetitionCategoryRepository
) {
    fun execute(competitionCategory: CompetitionCategoryDTO) {
        repository.setStatus(competitionCategory.id, CompetitionCategoryStatus.CANCELLED)
    }
}