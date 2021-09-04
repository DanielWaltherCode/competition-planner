package com.graphite.competitionplanner.domain.dto

import com.graphite.competitionplanner.domain.entity.DrawType

data class DrawTypeDTO(
    val id: Int,
    val name: String
) {
    constructor(entity: DrawType) : this(entity.id, entity.name)
}
