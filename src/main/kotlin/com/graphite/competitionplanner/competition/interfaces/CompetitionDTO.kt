package com.graphite.competitionplanner.competition.interfaces

import com.fasterxml.jackson.annotation.JsonFormat
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
    @JsonFormat(pattern = "yyyy-MM-dd")
    val startDate: LocalDate,
    @JsonFormat(pattern = "yyyy-MM-dd")
    val endDate: LocalDate
)