package com.graphite.competitionplanner.schedule.interfaces

import com.graphite.competitionplanner.match.domain.MatchType
import java.time.LocalDateTime

data class MatchTimeTableSlotDTO(
    val id: Int,
    val competitionId: Int,
    val startTime: LocalDateTime,
    val tableNumber: Int,
    val location: String,
    val competitionCategoryId: Int?,
    val matchType: MatchType?
)