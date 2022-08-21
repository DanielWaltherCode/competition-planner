package com.graphite.competitionplanner.category.interfaces

import com.graphite.competitionplanner.category.domain.CategoryType

data class CustomCategorySpec(
    val name: String,
    val type: CategoryType
) {
    init {
        require(name.isNotBlank() && name.isNotEmpty()) { "Custom category cannot have a blank or empty name" }
    }
}