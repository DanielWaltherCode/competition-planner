package com.graphite.competitionplanner.schedule.domain.interfaces

import com.graphite.competitionplanner.competitioncategory.entity.Match
import java.time.LocalDateTime

data class MatchDTO(
    val id: Int,
    val startTime: LocalDateTime?,
    val endTime: LocalDateTime?,
    val competitionCategoryId: Int,
    val matchType: String,
    val firstPlayer: List<Int>,
    val secondPlayer: List<Int>,
    val matchOrderNumber: Int,
    val groupOrRound: String
) {

    internal constructor(match: Match) : this(
        match.id,
        match.startTime,
        match.endTime,
        match.competitionCategoryId,
        match.type.value,
        match.firstTeamPlayerIds,
        match.secondTeamPlayerIds,
        match.orderNumber,
        match.groupOrRound
    )
}
