package com.graphite.competitionplanner.domain.dto

import com.graphite.competitionplanner.domain.entity.Player
import java.time.LocalDate

/**
 * This is the data transfer object of the Player entity class
 */
data class PlayerWithClubDTO(
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

    constructor(player: PlayerDTO, club: ClubDTO) : this(
        player.id,
        player.firstName,
        player.lastName,
        club,
        player.dateOfBirth
    )
}
