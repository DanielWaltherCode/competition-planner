package com.graphite.competitionplanner.competitioncategory.domain

import com.graphite.competitionplanner.domain.entity.CompetitionCategoryStatus
import com.graphite.competitionplanner.competitioncategory.domain.interfaces.ICompetitionCategoryRepository
import org.springframework.stereotype.Component

@Component
class CancelCompetitionCategory(
    val repository: ICompetitionCategoryRepository
) {
    fun execute(competitionCategoryId: Int) {
        repository.setStatus(competitionCategoryId, CompetitionCategoryStatus.CANCELLED)
    }
}