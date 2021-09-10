package com.graphite.competitionplanner.category.service

import com.graphite.competitionplanner.category.domain.interfaces.CategoryDTO
import com.graphite.competitionplanner.category.domain.GetCategories
import org.springframework.stereotype.Service

@Service
class CategoryService(
    val getCategories: GetCategories
) {

    fun getCategories(): List<CategoryDTO> {
        return getCategories.execute()
    }

}