package com.graphite.competitionplanner.category.api

import com.graphite.competitionplanner.category.domain.interfaces.CategoryDTO
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
    fun getCategories(): List<CategorySpec> {
        return categoryService.getCategories().map { CategorySpec(it.id, it.name, it.type) }
    }

}

data class CategorySpec(
    val id: Int,
    val name: String,
    val type: String
) {
    constructor(dto: CategoryDTO) : this(
        dto.id,
        dto.name,
        dto.type
    )
}