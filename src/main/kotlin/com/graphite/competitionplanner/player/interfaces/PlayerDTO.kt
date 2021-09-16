package com.graphite.competitionplanner.player.interfaces

import com.fasterxml.jackson.annotation.JsonFormat
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
    @JsonFormat(pattern = "yyyy-MM-dd")
    val dateOfBirth: LocalDate
)
