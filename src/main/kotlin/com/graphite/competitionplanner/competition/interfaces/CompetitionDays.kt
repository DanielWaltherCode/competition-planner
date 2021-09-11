package com.graphite.competitionplanner.competition.interfaces

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDate

data class CompetitionDays(
    @JsonFormat(pattern = "yyyy-MM-dd")
    val competitionDays: List<LocalDate>
)