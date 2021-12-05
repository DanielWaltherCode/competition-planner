package com.graphite.competitionplanner.competitioncategory.domain

import com.graphite.competitionplanner.category.interfaces.CategoryDTO
import com.graphite.competitionplanner.category.interfaces.CategorySpec
import com.graphite.competitionplanner.category.interfaces.ICategoryRepository
import com.graphite.competitionplanner.competitioncategory.interfaces.*
import com.graphite.competitionplanner.domain.entity.Round
import org.springframework.stereotype.Component

@Component
class AddCompetitionCategory(
    val repository: ICompetitionCategoryRepository,
    val categoryRepository: ICategoryRepository
) {

    fun execute(competitionId: Int, category: CategorySpec): CompetitionCategoryDTO {
        val availableCategories = categoryRepository.getAvailableCategories()
        val categoryDto = CategoryDTO(category.id, category.name, category.type)
        if (availableCategories.none { it == categoryDto }) {
            throw IllegalArgumentException("Not a valid category: $category")
        }

        val addedCategories = repository.getAll(competitionId)
        if (addedCategories.any { it.category == category }) {
            throw IllegalArgumentException("The category $category has already been added")
        }

        val spec = CompetitionCategorySpec(
            status = CompetitionCategoryStatus.ACTIVE,
            category = category,
            settings = getDefaultGeneralSettings(),
            gameSettings = getDefaultGameSettings()
        )

        return repository.store(competitionId, spec)
    }

    private fun getDefaultGeneralSettings(): GeneralSettingsSpec {
        return GeneralSettingsSpec(
            150f,
            DrawType.POOL_ONLY,
            4,
            2,
            PoolDrawStrategy.NORMAL
        )
    }

    private fun getDefaultGameSettings(): GameSettingsSpec {
        return GameSettingsSpec(
            5,
            11,
            2,
            Round.UNKNOWN,
            7,
            11,
            2,
            false,
            7,
            2,
            false
        )
    }
}
