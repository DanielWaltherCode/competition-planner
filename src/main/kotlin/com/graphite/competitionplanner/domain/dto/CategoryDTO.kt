package com.graphite.competitionplanner.domain.dto

import com.graphite.competitionplanner.domain.entity.Category

data class CategoryDTO(val id: Int, val name: String) {
    constructor(category: Category) : this(category.id, category.name)
}
