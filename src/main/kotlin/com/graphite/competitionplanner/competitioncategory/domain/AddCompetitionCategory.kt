package com.graphite.competitionplanner.competitioncategory.domain

import com.graphite.competitionplanner.category.interfaces.CategoryDTO
import com.graphite.competitionplanner.category.interfaces.CategorySpec
import com.graphite.competitionplanner.category.interfaces.ICategoryRepository
import com.graphite.competitionplanner.competitioncategory.interfaces.*
import com.graphite.competitionplanner.draw.interfaces.Round
import com.graphite.competitionplanner.schedule.api.CategoryStartTimeSpec
import com.graphite.competitionplanner.schedule.service.ScheduleService
import org.springframework.stereotype.Component

@Component
class AddCompetitionCategory(
    val repository: ICompetitionCategoryRepository,
    val categoryRepository: ICategoryRepository,
    val scheduleService: ScheduleService
) {

    fun execute(competitionId: Int, category: CategorySpec): CompetitionCategoryDTO {
        val availableCategories: List<CategoryDTO> = categoryRepository.getAvailableCategories()
        val categoryDto = CategoryDTO(category.id, category.name, category.type)
        if (availableCategories.none { it == categoryDto }) {
            throw IllegalArgumentException("Not a valid category: $category")
        }

        val addedCategories: List<CompetitionCategoryDTO> = repository.getAll(competitionId)
        if (addedCategories.any { it.category == category }) {
            throw IllegalArgumentException("The category $category has already been added")
        }

        val spec = CompetitionCategorySpec(
            status = CompetitionCategoryStatus.ACTIVE,
            category = category,
            settings = getDefaultGeneralSettings(),
            gameSettings = getDefaultGameSettings()
        )

        val competitionCategory = repository.store(competitionId, spec)
        scheduleService.addCategoryStartTime(competitionCategory.id, CategoryStartTimeSpec(null, null, null))
        return competitionCategory
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
