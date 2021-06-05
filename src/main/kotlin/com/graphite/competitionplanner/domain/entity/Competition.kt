package com.graphite.competitionplanner.domain.entity

import java.time.LocalDate

data class Competition(
    val id: Int,
    val location: Location,
    val name: String,
    val welcomeText: String,
    val organizer: Club,
    val startDate: LocalDate,
    val endDate: LocalDate
) {
    init {
        require(name.isNotEmpty())
        require(name.isNotBlank())
        require(startDate.isBefore(endDate) || startDate == endDate)
    }
}