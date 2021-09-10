package com.graphite.competitionplanner.domain.entity

import com.graphite.competitionplanner.competition.domain.interfaces.LocationDTO

class Location(
    val name: String
) {
    init {
        require(name.isNotEmpty())
        require(name.isNotBlank())
    }

    constructor(dto: LocationDTO) : this(dto.name)
}
