package com.graphite.competitionplanner.domain.entity

import com.graphite.competitionplanner.domain.dto.ClubDTO

data class Club(val id: Int, val name: String, val address: String) {
    init {
        require(name.isNotEmpty()) { "Club name cannot be empty" }
        require(address.isNotEmpty()) { "Club address cannot be empty" }
    }

    constructor(dto: ClubDTO) : this(dto.id, dto.name, dto.address)
}