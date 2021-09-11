package com.graphite.competitionplanner.competition.interfaces

import java.time.LocalDate

/**
 * This represents a competition, and compared to the CompetitionEntityDTO this only holds references to the
 * organizing club. This is mainly used by the repositories.
 */
data class CompetitionDTO(
    val id: Int,
    val location: LocationDTO,
    val name: String,
    val welcomeText: String,
    val organizerId: Int,
    val startDate: LocalDate,
    val endDate: LocalDate
)