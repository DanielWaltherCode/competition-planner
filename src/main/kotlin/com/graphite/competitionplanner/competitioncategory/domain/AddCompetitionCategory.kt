package com.graphite.competitionplanner.competitioncategory.domain

import com.graphite.competitionplanner.category.interfaces.CategoryDTO
import com.graphite.competitionplanner.category.interfaces.CategorySpec
import com.graphite.competitionplanner.category.interfaces.ICategoryRepository
import com.graphite.competitionplanner.common.exception.BadRequestException
import com.graphite.competitionplanner.common.exception.BadRequestType
import com.graphite.competitionplanner.competitioncategory.interfaces.*
import com.graphite.competitionplanner.draw.interfaces.Round
import org.springframework.stereotype.Component

@Component
class AddCompetitionCategory(
    val repository: ICompetitionCategoryRepository,
    val categoryRepository: ICategoryRepository,
) {

    fun execute(competitionId: Int, category: CategorySpec): CompetitionCategoryDTO {
        val availableCategories: List<CategoryDTO> = categoryRepository.getAvailableCategories(competitionId)
        val categoryDto = CategoryDTO(category.id, category.name, category.type)
        if (availableCategories.none { it == categoryDto }) {
            throw BadRequestException(BadRequestType.CATEGORY_NOT_VALID, "Not a valid category: $category")
        }

        val addedCategories: List<CompetitionCategoryDTO> = repository.getAll(competitionId)
        if (addedCategories.any { it.category == category }) {
            throw BadRequestException(BadRequestType.CATEGORY_ALREADY_ADDED,
                    "The category $category has already been added")
        }

        val spec = CompetitionCategorySpec(
            status = CompetitionCategoryStatus.OPEN_FOR_REGISTRATION,
            category = category,
            settings = getDefaultGeneralSettings(),
            gameSettings = getDefaultGameSettings()
        )

        return repository.store(competitionId, spec)
    }

    private fun getDefaultGeneralSettings(): GeneralSettingsDTO {
        return GeneralSettingsDTO(
            150f,
            DrawType.POOL_AND_CUP,
            4,
            2,
            PoolDrawStrategy.NORMAL
        )
    }

    private fun getDefaultGameSettings(): GameSettingsDTO {
        return GameSettingsDTO(
            5,
            11,
            2,
            Round.UNKNOWN,
            5,
            11,
            2,
            false,
            7,
            2,
            false
        )
    }
}
