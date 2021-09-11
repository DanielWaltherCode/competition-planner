package com.graphite.competitionplanner.club.interfaces


data class ClubDTO(val id: Int, val name: String, val address: String) {

    constructor(id: Int, dto: ClubDTO) : this(id, dto.name, dto.address)
}