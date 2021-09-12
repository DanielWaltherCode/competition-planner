package com.graphite.competitionplanner.competition.interfaces

import com.fasterxml.jackson.annotation.JsonFormat
import com.graphite.competitionplanner.club.interfaces.ClubDTO
import java.time.LocalDate

data class CompetitionWithClubDTO(
    val id: Int,
    val location: LocationDTO,
    val name: String,
    val welcomeText: String,
    val organizerClub: ClubDTO,
    @JsonFormat(pattern = "yyyy-MM-dd")
    val startDate: LocalDate,
    @JsonFormat(pattern = "yyyy-MM-dd")
    val endDate: LocalDate
)