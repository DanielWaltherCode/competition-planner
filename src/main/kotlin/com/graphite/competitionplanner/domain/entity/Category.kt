package com.graphite.competitionplanner.domain.entity

import com.graphite.competitionplanner.category.domain.interfaces.CategoryDTO

/**
 * The Category represents a unique category. Two categories cannot have the same name.
 */
data class Category(
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
        CategoryType.valueOf(dto.type)
    )
}

enum class CategoryType {
    SINGLES, DOUBLES, BYE
}