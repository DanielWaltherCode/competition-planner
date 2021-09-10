package com.graphite.competitionplanner.domain.entity

import com.graphite.competitionplanner.competition.domain.interfaces.CompetitionDTO
import java.time.LocalDate

internal data class Competition(
    val id: Int,
    val location: Location,
    val name: String,
    val welcomeText: String,
    val startDate: LocalDate,
    val endDate: LocalDate
) {
    lateinit var organizer: Club

    init {
        require(name.isNotEmpty())
        require(name.isNotBlank())
        require(startDate.isBefore(endDate) || startDate == endDate)
    }

    constructor(dto: CompetitionDTO) : this(
        dto.id,
        Location(dto.location),
        dto.name,
        dto.welcomeText,
        dto.startDate,
        dto.endDate
    )
}