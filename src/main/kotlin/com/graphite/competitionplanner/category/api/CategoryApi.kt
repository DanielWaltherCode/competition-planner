package com.graphite.competitionplanner.category.api

import com.graphite.competitionplanner.category.interfaces.CategoryDTO
import com.graphite.competitionplanner.category.service.CategoryService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("category")
class CategoryApi(
    val categoryService: CategoryService
) {

    @GetMapping
    fun getCategories(): List<CategoryDTO> {
        return categoryService.getCategories()
    }

}
