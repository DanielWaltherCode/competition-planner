package com.graphite.competitionplanner.domain.entity

import com.graphite.competitionplanner.domain.dto.MatchDTO
import java.time.LocalDateTime

internal data class Match(
    val id: Int,
    val startTime: LocalDateTime?,
    val endTime: LocalDateTime?,
    val competitionCategory: CompetitionCategory,
    val type: MatchType,
    val firstPlayer: List<Player>,
    val secondPlayer: List<Player>,
    val orderNumber: Int,
    val groupOrRound: String // Either group name (e.g. Group "A") or the round like Round of 64, Quarterfinals
) {
    init {
        require(firstPlayer.map { it.id }.intersect(secondPlayer.map { it.id }).none()
        ) { "At least one player belongs to both teams" + secondPlayer.map { id } + firstPlayer.map { id } }
    }

    constructor(dto: MatchDTO) : this(dto.id, dto.startTime, dto.endTime,
        CompetitionCategory(dto.competitionCategoryId), MatchType(dto.matchType), dto.firstPlayer.map { Player(it) },
        dto.secondPlayer.map { Player(it) }, dto.matchOrderNumber, dto.groupOrRound
    )
}