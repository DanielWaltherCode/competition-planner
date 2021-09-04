package com.graphite.competitionplanner.domain.entity

import com.graphite.competitionplanner.domain.dto.DrawTypeDTO

data class DrawType(
    val id: Int,
    val name: String
) {
    constructor(dto: DrawTypeDTO) : this(dto.id, dto.name)
}
