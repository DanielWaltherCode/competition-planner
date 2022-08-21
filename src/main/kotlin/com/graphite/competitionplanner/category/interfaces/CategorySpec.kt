package com.graphite.competitionplanner.category.interfaces

import com.graphite.competitionplanner.category.domain.CategoryType

data class CategorySpec(
    val id: Int,
    val name: String,
    val type: CategoryType
) {
    init {
        require(name.isNotEmpty())
        require(name.isNotBlank())
    }

    constructor(dto: CategoryDTO) : this(
        dto.id,
        dto.name,
        dto.type
    )
}