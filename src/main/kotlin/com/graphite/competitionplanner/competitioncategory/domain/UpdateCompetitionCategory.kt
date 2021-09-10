package com.graphite.competitionplanner.competitioncategory.domain

import com.graphite.competitionplanner.competitioncategory.domain.interfaces.CompetitionCategoryUpdateDTO
import com.graphite.competitionplanner.domain.entity.GameSettings
import com.graphite.competitionplanner.domain.entity.GeneralSettings
import com.graphite.competitionplanner.competitioncategory.domain.interfaces.ICompetitionCategoryRepository
import org.springframework.stereotype.Component

@Component
class UpdateCompetitionCategory(
    val repository: ICompetitionCategoryRepository
) {

    fun execute(dto: CompetitionCategoryUpdateDTO) {
        // Validate
        GeneralSettings(dto.settings)
        GameSettings(dto.gameSettings)

        // Store
        repository.update(dto)
    }

}