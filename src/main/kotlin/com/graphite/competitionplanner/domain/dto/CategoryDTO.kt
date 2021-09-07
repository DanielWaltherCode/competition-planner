package com.graphite.competitionplanner.domain.dto

import com.graphite.competitionplanner.domain.entity.Category

data class CategoryDTO(
    val id: Int,
    val name: String,
    val type: String
) {
    constructor(entity: Category) : this(
        entity.id,
        entity.name,
        entity.type.name
    )
}
