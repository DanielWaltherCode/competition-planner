package com.graphite.competitionplanner.category.interfaces

import com.graphite.competitionplanner.category.domain.CategoryType

data class CustomCategorySpec(
    val name: String,
    val type: CategoryType
)