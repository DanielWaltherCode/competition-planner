package com.graphite.competitionplanner.competitioncategory.domain

import com.graphite.competitionplanner.competitioncategory.interfaces.CompetitionCategoryDTO
import com.graphite.competitionplanner.competitioncategory.interfaces.ICompetitionCategoryRepository
import org.springframework.stereotype.Component

@Component
class GetCompetitionCategory(
    val repository: ICompetitionCategoryRepository
) {

    fun execute(competitionCategoryId: Int): CompetitionCategoryDTO {
        return repository.get(competitionCategoryId)
    }

}