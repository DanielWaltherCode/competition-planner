package com.graphite.competitionplanner.category.interfaces

import com.graphite.competitionplanner.category.domain.CategoryType

data class CategoryDTO(
    val id: Int,
    val name: String,
    val type: CategoryType
)