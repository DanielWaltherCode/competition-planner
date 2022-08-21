package com.graphite.competitionplanner.category.interfaces

import com.graphite.competitionplanner.category.domain.CategoryType

data class CustomCategorySpec(
    val name: String,
    val type: CategoryType
) {
    init {
        require(name.isNotBlank()) { "Custom category cannot have a blank name" }
        require(name.isNotEmpty()) { "Custom category cannot have an empty name."}
    }
}