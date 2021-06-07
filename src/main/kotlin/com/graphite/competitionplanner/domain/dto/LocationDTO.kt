package com.graphite.competitionplanner.domain.dto;

import com.graphite.competitionplanner.domain.entity.Location

data class LocationDTO(
    val name: String
) {
    constructor(entity: Location) : this(entity.name)
}
