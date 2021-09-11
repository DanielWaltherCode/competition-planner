package com.graphite.competitionplanner.competition.interfaces

import java.time.LocalDate

data class CompetitionSpec(
    val location: LocationSpec,
    val name: String,
    val welcomeText: String,
    val organizingClubId: Int,
    val startDate: LocalDate,
    val endDate: LocalDate?
) {
    init {
        require(name.isNotEmpty())
        require(name.isNotBlank())
        require(startDate.isBefore(endDate) || startDate == endDate)
    }
}