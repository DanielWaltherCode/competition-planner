package com.graphite.competitionplanner.domain.dto

import com.graphite.competitionplanner.domain.entity.Player
import java.time.LocalDate

data class PlayerDTO(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val club: ClubDTO,
    val dateOfBirth: LocalDate
) {
    constructor(player: Player) : this(
        player.id,
        player.firstName,
        player.lastName,
        ClubDTO(player.club),
        player.dateOfBirth
    )

    constructor(id: Int, dto: PlayerDTO) : this(
        id,
        dto.firstName,
        dto.lastName,
        dto.club,
        dto.dateOfBirth
    )
}
