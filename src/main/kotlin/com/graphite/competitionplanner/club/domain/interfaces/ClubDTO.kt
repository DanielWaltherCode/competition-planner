package com.graphite.competitionplanner.club.domain.interfaces

import com.graphite.competitionplanner.domain.entity.Club

data class ClubDTO(val id: Int, val name: String, val address: String) {
    constructor(club: Club) : this(club.id, club.name, club.address)

    constructor(id: Int, dto: ClubDTO) : this(id, dto.name, dto.address)
}