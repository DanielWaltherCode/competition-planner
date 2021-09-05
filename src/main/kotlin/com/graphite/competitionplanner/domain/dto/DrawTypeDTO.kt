package com.graphite.competitionplanner.domain.dto

import com.graphite.competitionplanner.domain.entity.DrawType

data class DrawTypeDTO(
    val name: String
) {
    constructor(entity: DrawType) : this(entity.name)
}
