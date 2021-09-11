package com.graphite.competitionplanner.competitioncategory.domain

import com.graphite.competitionplanner.category.interfaces.CategoryDTO
import com.graphite.competitionplanner.category.interfaces.ICategoryRepository
import com.graphite.competitionplanner.competitioncategory.domain.interfaces.CompetitionCategoryDTO
import com.graphite.competitionplanner.competitioncategory.domain.interfaces.GameSettingsDTO
import com.graphite.competitionplanner.competitioncategory.domain.interfaces.GeneralSettingsDTO
import com.graphite.competitionplanner.competitioncategory.domain.interfaces.ICompetitionCategoryRepository
import com.graphite.competitionplanner.domain.entity.*
import org.springframework.stereotype.Component

@Component
class AddCompetitionCategory(
    val repository: ICompetitionCategoryRepository,
    val categoryRepository: ICategoryRepository
) {

    fun execute(competitionId: Int, category: CategoryDTO): CompetitionCategoryDTO {
        val availableCategories = categoryRepository.getAvailableCategories()
        if (availableCategories.none { it == category }) {
            throw IllegalArgumentException("Not a valid category: $category")
        }

        val addedCategories = repository.getAll(competitionId)
        if (addedCategories.any { it.category == category }) {
            throw IllegalArgumentException("The category $category has already been added")
        }

        val dto = CompetitionCategoryDTO(
            id = 0,
            status = CompetitionCategoryStatus.ACTIVE.name,
            category = category,
            settings = GeneralSettingsDTO(getDefaultGeneralSettings()),
            gameSettings = GameSettingsDTO(getDefaultGameSettings())
        )

        return repository.store(competitionId, dto)
    }

    private fun getDefaultGeneralSettings(): GeneralSettings {
        return GeneralSettings(
            150f,
            DrawType.POOL_ONLY,
            4,
            2,
            PoolDrawStrategy.NORMAL
        )
    }

    private fun getDefaultGameSettings(): GameSettings {
        return GameSettings(
            5,
            11,
            2,
            Round.UNKNOWN,
            7,
            11,
            2,
            false,
            2,
            2
        )
    }
}
