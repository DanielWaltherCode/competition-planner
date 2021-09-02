package com.graphite.competitionplanner.domain.usecase.competition

import com.graphite.competitionplanner.domain.dto.CategoryDTO
import com.graphite.competitionplanner.domain.interfaces.ICompetitionCategoryRepository
import org.springframework.stereotype.Component
import java.lang.IllegalArgumentException

@Component
class AddCompetitionCategory(
    val repository: ICompetitionCategoryRepository
) {

    fun execute(competitionId: Int, dto: CategoryDTO) {
        val availableCategories = repository.getAvailableCategories()
        if (availableCategories.none { it == dto }) {
            throw IllegalArgumentException("Not a valid category: $dto")
        }

        val addedCategories = repository.getCompetitionCategoriesIn(competitionId)
        if (addedCategories.any { it.category == dto }) {
            throw IllegalArgumentException("The category $dto has already been added")
        }

        repository.addCompetitionCategoryTo(competitionId, dto)
    }
}
