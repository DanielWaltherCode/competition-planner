package com.graphite.competitionplanner.competition.interfaces

import java.time.LocalDate

data class CompetitionUpdateSpec(
    val location: LocationSpec,
    val name: String,
    val welcomeText: String,
    val startDate: LocalDate,
    val endDate: LocalDate?
) {
    init {
        require(name.isNotEmpty())
        require(name.isNotBlank())
        require(startDate.isBefore(endDate) || startDate == endDate)
    }
}
