package com.graphite.competitionplanner.competitioncategory.domain.interfaces

import com.graphite.competitionplanner.domain.entity.DrawType

data class DrawTypeDTO(
    val name: String
) {
    constructor(entity: DrawType) : this(entity.name)
}
