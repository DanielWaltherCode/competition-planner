package com.graphite.competitionplanner.category.domain

import com.graphite.competitionplanner.category.interfaces.CustomCategorySpec
import com.graphite.competitionplanner.category.interfaces.CategorySpec
import com.graphite.competitionplanner.category.interfaces.ICategoryRepository
import com.graphite.competitionplanner.competitioncategory.domain.AddCompetitionCategory
import com.graphite.competitionplanner.competitioncategory.interfaces.CompetitionCategoryDTO
import org.springframework.stereotype.Service

@Service
class AddCustomCategory(
    val categoryRepository: ICategoryRepository,
    val addCompetitionCategory: AddCompetitionCategory
) {

    fun execute(competitionId: Int, customCategorySpec: CustomCategorySpec): CompetitionCategoryDTO {
        val addedCategory = categoryRepository.addCustomCategory(competitionId, customCategorySpec)
        return addCompetitionCategory.execute(
            competitionId,
            CategorySpec(addedCategory.id, addedCategory.name, addedCategory.type)
        )
    }
}