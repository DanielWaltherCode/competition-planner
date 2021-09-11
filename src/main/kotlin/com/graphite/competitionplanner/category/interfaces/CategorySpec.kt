package com.graphite.competitionplanner.category.interfaces

data class CategorySpec(
    val id: Int,
    val name: String,
    val type: String
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