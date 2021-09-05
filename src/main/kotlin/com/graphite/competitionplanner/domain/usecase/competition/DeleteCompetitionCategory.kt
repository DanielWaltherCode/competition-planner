package com.graphite.competitionplanner.domain.usecase.competition

import com.graphite.competitionplanner.domain.interfaces.ICompetitionCategoryRepository
import org.springframework.stereotype.Component

@Component
class DeleteCompetitionCategory(
    val repository: ICompetitionCategoryRepository
) {

    fun execute(competitionCategoryId: Int) {
        repository.delete(competitionCategoryId)
    }
}