package com.graphite.competitionplanner.competitioncategory.domain

import com.graphite.competitionplanner.competitioncategory.interfaces.ICompetitionCategoryRepository
import org.springframework.stereotype.Component

@Component
class DeleteCompetitionCategory(
    val repository: ICompetitionCategoryRepository
) {

    fun execute(competitionCategoryId: Int) {
        repository.delete(competitionCategoryId)
    }
}