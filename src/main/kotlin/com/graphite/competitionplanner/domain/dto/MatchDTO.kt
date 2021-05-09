package com.graphite.competitionplanner.domain.dto

import com.graphite.competitionplanner.domain.entity.Match
import java.time.LocalDateTime

data class MatchDTO(
    val id: Int,
    val startTime: LocalDateTime?,
    val endTime: LocalDateTime?,
    val competitionCategoryId: Int,
    val matchType: String,
    val firstPlayer: List<PlayerDTO>,
    val secondPlayer: List<PlayerDTO>,
    val matchOrderNumber: Int,
    val groupOrRound: String
) {
//    init {
//        require(startTime?.isBefore(endTime) ?: true)
//    } TODO How does this work?

    internal constructor(match: Match) : this(match, null, null)

    internal constructor(match: Match, startTime: LocalDateTime?, endTime: LocalDateTime?) : this(
        match.id,
        startTime,
        endTime,
        match.competitionCategory.id,
        match.type.value,
        match.firstPlayer.map { PlayerDTO(it) },
        match.secondPlayer.map { PlayerDTO(it) },
        match.orderNumber,
        match.groupOrRound
    )
}
