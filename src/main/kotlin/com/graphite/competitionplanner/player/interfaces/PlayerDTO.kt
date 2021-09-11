package com.graphite.competitionplanner.player.interfaces

import java.time.LocalDate

/**
 * This is the data transfer object for a player. Compare to the PlayerWithDTO this only holds
 * a reference to the club.
 */
data class PlayerDTO(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val clubId: Int,
    val dateOfBirth: LocalDate
)
