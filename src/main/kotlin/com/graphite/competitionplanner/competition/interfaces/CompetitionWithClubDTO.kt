package com.graphite.competitionplanner.competition.interfaces

import com.graphite.competitionplanner.club.interfaces.ClubDTO
import java.time.LocalDate

data class CompetitionWithClubDTO(
    val id: Int,
    val location: LocationDTO,
    val name: String,
    val welcomeText: String,
    val organizerClub: ClubDTO,
    val startDate: LocalDate,
    val endDate: LocalDate
)