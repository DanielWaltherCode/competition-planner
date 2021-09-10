package com.graphite.competitionplanner.competitioncategory.domain

import com.graphite.competitionplanner.domain.dto.CompetitionCategoryDTO
import com.graphite.competitionplanner.competitioncategory.domain.interfaces.ICompetitionCategoryRepository
import org.springframework.stereotype.Component

@Component
class GetCompetitionCategory(
    val repository: ICompetitionCategoryRepository
) {

    fun execute(competitionCategoryId: Int): CompetitionCategoryDTO {
        return repository.get(competitionCategoryId)
    }

}