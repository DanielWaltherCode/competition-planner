package com.graphite.competitionplanner.api

import com.graphite.competitionplanner.api.competition.CategorySpec
import com.graphite.competitionplanner.repositories.competition.CategoryRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("category")
class CategoryApi(val categoryRepository: CategoryRepository) {

    // Returns a list of all possible categories
    @GetMapping
    fun getCategories(): List<CategorySpec> {
        val categoryRecords = categoryRepository.getCategories()
        return categoryRecords.map { CategorySpec(it.id, it.categoryName, it.categoryType) }
    }

}
