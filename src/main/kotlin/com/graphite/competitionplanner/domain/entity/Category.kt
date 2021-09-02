package com.graphite.competitionplanner.domain.entity

import com.graphite.competitionplanner.domain.dto.CategoryDTO

/**
 * The Category represents a unique category. Two categories cannot have the same name.
 */
data class Category(
    val id: Int,
    val name: String
) {

    init {
        require(name.isNotEmpty())
        require(name.isNotBlank())
    }

    constructor(dto: CategoryDTO) : this(
        dto.id,
        dto.name
    )
}
