package com.graphite.competitionplanner.player.interfaces

import com.graphite.competitionplanner.club.interfaces.ClubDTO
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

    constructor(player: PlayerDTO, club: ClubDTO) : this(
        player.id,
        player.firstName,
        player.lastName,
        club,
        player.dateOfBirth
    )
}
