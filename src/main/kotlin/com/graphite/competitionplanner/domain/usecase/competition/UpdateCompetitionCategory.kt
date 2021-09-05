package com.graphite.competitionplanner.domain.usecase.competition

import com.graphite.competitionplanner.domain.dto.CompetitionCategoryDTO
import com.graphite.competitionplanner.domain.entity.CompetitionCategory
import com.graphite.competitionplanner.domain.interfaces.ICompetitionCategoryRepository
import org.springframework.stereotype.Component

@Component
class UpdateCompetitionCategory(
    val repository: ICompetitionCategoryRepository
) {

    fun execute(dto: CompetitionCategoryDTO) {
        // Validate
        CompetitionCategory(dto)

        // Store
        repository.update(dto)
    }

}