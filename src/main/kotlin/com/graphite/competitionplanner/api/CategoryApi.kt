package com.graphite.competitionplanner.api

import com.graphite.competitionplanner.repositories.competition.CategoryRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("category")
class CategoryApi(val categoryRepository: CategoryRepository) {

    // Returns a list of all possible categories
    @GetMapping
    fun getCategories(): List<CategoryDTO> {
        val categoryRecords = categoryRepository.getCategories()
        return categoryRecords.map { CategoryDTO(it.id, it.categoryName, it.categoryType) }
    }

}

data class CategoryDTO(
    val id: Int,
    val name: String,
    val type: String
)
