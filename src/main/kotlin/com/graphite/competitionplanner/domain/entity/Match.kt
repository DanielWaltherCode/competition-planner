package com.graphite.competitionplanner.domain.entity

import com.graphite.competitionplanner.schedule.domain.interfaces.MatchDTO
import java.time.LocalDateTime

internal data class Match(
    val id: Int,
    val competitionCategoryId: Int,
    val startTime: LocalDateTime?,
    val endTime: LocalDateTime?,
    val type: MatchType,
    val firstTeamPlayerIds: List<Int>,
    val secondTeamPlayerIds: List<Int>,
    val orderNumber: Int,
    val groupOrRound: String // Either group name (e.g. Group "A") or the round like Round of 64, Quarterfinals
) {
    init {
        require(
            firstTeamPlayerIds.intersect(secondTeamPlayerIds).none()
        ) { "At least one player belongs to both teams $secondTeamPlayerIds $firstTeamPlayerIds" }
        require(startTime?.isBefore(endTime) ?: true) { "When specified, start time must be before end time" }
    }

    constructor(match: Match, startTime: LocalDateTime?, endTime: LocalDateTime?) : this(
        match.id,
        match.competitionCategoryId,
        startTime,
        endTime,
        match.type,
        match.firstTeamPlayerIds,
        match.secondTeamPlayerIds,
        match.orderNumber,
        match.groupOrRound
    )

    constructor(dto: MatchDTO) : this(
        dto.id,
        dto.competitionCategoryId,
        dto.startTime,
        dto.endTime,
        MatchType(dto.matchType),
        dto.firstPlayer,
        dto.secondPlayer,
        dto.matchOrderNumber,
        dto.groupOrRound
    )
}