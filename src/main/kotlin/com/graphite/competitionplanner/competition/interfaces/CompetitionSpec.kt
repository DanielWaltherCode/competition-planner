package com.graphite.competitionplanner.competition.interfaces

import java.time.LocalDate

data class CompetitionSpec(
    val location: LocationSpec,
    val name: String,
    val welcomeText: String,
    val organizingClubId: Int,
    val competitionLevel: String,
    val startDate: LocalDate,
    val endDate: LocalDate?
) {
    init {
        require(name.isNotEmpty()) { "Name cannot be empty." }
        require(name.isNotBlank()) { "Name cannot be blank." }
        require(startDate.isBefore(endDate) || startDate == endDate) { "Start date has to be before or on the same day as end date." }
    }
}