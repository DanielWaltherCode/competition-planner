package com.graphite.competitionplanner.category.api

import com.graphite.competitionplanner.category.domain.AddCustomCategory
import com.graphite.competitionplanner.category.domain.GetCategories
import com.graphite.competitionplanner.category.interfaces.CategoryDTO
import com.graphite.competitionplanner.category.domain.CategoryType
import com.graphite.competitionplanner.competitioncategory.interfaces.CompetitionCategoryDTO
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("category/{competitionId}")
class CategoryApi(
    val getCategories: GetCategories,
    val addCustomCategory: AddCustomCategory
) {

    @GetMapping
    fun getCategories(@PathVariable competitionId: Int): List<CategoryDTO> {
        return getCategories.execute(competitionId)
    }

    @PostMapping
    fun addCustomCategory(@PathVariable competitionId: Int, @RequestBody category: CustomCategorySpec): CompetitionCategoryDTO {
       return addCustomCategory.execute(competitionId, category)
    }

}

data class CustomCategorySpec(
    val name: String,
    val type: CategoryType
)