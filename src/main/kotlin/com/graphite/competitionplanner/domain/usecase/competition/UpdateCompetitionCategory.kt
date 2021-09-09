package com.graphite.competitionplanner.domain.usecase.competition

import com.graphite.competitionplanner.domain.dto.CompetitionCategoryDTO
import com.graphite.competitionplanner.domain.dto.CompetitionCategoryUpdateDTO
import com.graphite.competitionplanner.domain.entity.CompetitionCategory
import com.graphite.competitionplanner.domain.entity.GameSettings
import com.graphite.competitionplanner.domain.entity.GeneralSettings
import com.graphite.competitionplanner.domain.interfaces.ICompetitionCategoryRepository
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