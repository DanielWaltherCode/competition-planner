package com.graphite.competitionplanner.domain.dto

import java.time.LocalDate

/**
 * This data transfer object represents a competition entity
 */
data class CompetitionEntityDTO(
    val id: Int,
    val location: LocationDTO,
    val name: String,
    val welcomeText: String,
    val organizer: ClubDTO,
    val startDate: LocalDate,
    val endDate: LocalDate
)