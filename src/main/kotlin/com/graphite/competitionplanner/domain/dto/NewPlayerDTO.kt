package com.graphite.competitionplanner.domain.dto

import java.time.LocalDate

/**
 * This is the data transfer object when creating a new player. It has no ID.
 */
data class NewPlayerDTO(
    val firstName: String,
    val lastName: String,
    val clubId: Int,
    val dateOfBirth: LocalDate
)