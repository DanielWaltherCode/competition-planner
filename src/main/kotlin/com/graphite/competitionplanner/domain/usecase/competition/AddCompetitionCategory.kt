package com.graphite.competitionplanner.domain.usecase.competition

import com.graphite.competitionplanner.domain.dto.GeneralSettingsDTO
import com.graphite.competitionplanner.domain.dto.CategoryDTO
import com.graphite.competitionplanner.domain.dto.CompetitionCategoryDTO
import com.graphite.competitionplanner.domain.dto.GameSettingsDTO
import com.graphite.competitionplanner.domain.entity.PoolDrawStrategy
import com.graphite.competitionplanner.domain.entity.DrawType
import com.graphite.competitionplanner.domain.entity.GameSettings
import com.graphite.competitionplanner.domain.entity.GeneralSettings
import com.graphite.competitionplanner.domain.interfaces.ICompetitionCategoryRepository
import org.springframework.stereotype.Component
import java.lang.IllegalArgumentException

@Component
class AddCompetitionCategory(
    val repository: ICompetitionCategoryRepository
) {

    fun execute(competitionId: Int, category: CategoryDTO): CompetitionCategoryDTO {
        val availableCategories = repository.getAvailableCategories()
        if (availableCategories.none { it == category }) {
            throw IllegalArgumentException("Not a valid category: $category")
        }

        val addedCategories = repository.getCompetitionCategoriesIn(competitionId)
        if (addedCategories.any { it.category == category }) {
            throw IllegalArgumentException("The category $category has already been added")
        }

        val dto = CompetitionCategoryDTO(
            id = 0,
            category = category,
            settings = GeneralSettingsDTO(getDefaultGeneralSettings()),
            gameSettings = GameSettingsDTO(getDefaultGameSettings())
        )

        return repository.addCompetitionCategoryTo(competitionId, dto)
    }

    private fun getDefaultGeneralSettings(): GeneralSettings {
        return GeneralSettings(
            150f,
            DrawType(repository.getDrawType("POOL")),
            4,
            2,
            PoolDrawStrategy(repository.getPoolDrawStrategy("NORMAL"))
        )
    }

    private fun getDefaultGameSettings(): GameSettings {
        return GameSettings(
            5,
            11,
            2,
            7,
            11,
            2,
            2,
            2
        )
    }
}
