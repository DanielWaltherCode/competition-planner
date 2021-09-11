package com.graphite.competitionplanner.category.service

import com.graphite.competitionplanner.category.domain.GetCategories
import com.graphite.competitionplanner.category.interfaces.CategoryDTO
import org.springframework.stereotype.Service

@Service
class CategoryService(
    val getCategories: GetCategories
) {

    fun getCategories(): List<CategoryDTO> {
        return getCategories.execute()
    }

}