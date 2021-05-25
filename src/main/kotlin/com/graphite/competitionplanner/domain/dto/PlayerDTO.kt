package com.graphite.competitionplanner.domain.dto

import java.time.LocalDate

/**
 * This is the data transfer object for a player. Compare to the PlayerEntityDTO this only holds
 * a reference to the club.
 */
data class PlayerDTO(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val clubId: Int,
    val dateOfBirth: LocalDate
) {
    constructor(id: Int, dto: NewPlayerDTO) : this(
        id,
        dto.firstName,
        dto.lastName,
        dto.clubId,
        dto.dateOfBirth
    )
}
