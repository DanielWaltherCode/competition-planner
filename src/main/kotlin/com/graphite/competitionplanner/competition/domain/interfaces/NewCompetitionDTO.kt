package com.graphite.competitionplanner.competition.domain.interfaces

import java.time.LocalDate

/**
 * This is the data transfer object when creating a new competition. It has no ID
 */
data class NewCompetitionDTO(
    val name: String,
    val location: String,
    val welcomeText: String,
    val organizingClubId: Int,
    val startDate: LocalDate,
    val endDate: LocalDate
)