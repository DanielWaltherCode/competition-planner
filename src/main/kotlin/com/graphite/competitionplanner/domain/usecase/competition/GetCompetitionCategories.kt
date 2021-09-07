package com.graphite.competitionplanner.domain.usecase.competition

import com.graphite.competitionplanner.domain.dto.CompetitionCategoryDTO
import com.graphite.competitionplanner.domain.interfaces.ICompetitionCategoryRepository
import org.springframework.stereotype.Component

@Component
class GetCompetitionCategories(
    val repository: ICompetitionCategoryRepository
) {
    fun execute(competitionId: Int): List<CompetitionCategoryDTO> {
        return repository.getAll(competitionId)
    }
}