package com.graphite.competitionplanner.category.api

import com.graphite.competitionplanner.category.domain.GetCategories
import com.graphite.competitionplanner.category.interfaces.CategoryDTO
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("category")
class CategoryApi(
    val getCategories: GetCategories
) {

    @GetMapping
    fun getCategories(): List<CategoryDTO> {
        return getCategories.execute()
    }

}
