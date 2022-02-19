package com.graphite.competitionplanner.match.domain

import com.graphite.competitionplanner.draw.interfaces.Round
import java.time.LocalDateTime

sealed class Match(
    val id: Int,
    val competitionCategoryId: Int,
    var firstRegistrationId: Int,
    var secondRegistrationId: Int,
    val wasWalkOver: Boolean,
    var winner: Int?,
    val startTime: LocalDateTime? = null,
    val endTime: LocalDateTime? = null,
    var result: List<GameResult> = emptyList()
)

class PoolMatch(
    val name: String,
    id: Int,
    competitionCategoryId: Int,
    firstRegistrationId: Int,
    secondRegistrationId: Int,
    wasWalkOver: Boolean,
    winner: Int?
) : Match(id, competitionCategoryId, firstRegistrationId, secondRegistrationId, wasWalkOver, winner)

class PlayoffMatch(
    val round: Round,
    val orderNumber: Int,
    id: Int,
    competitionCategoryId: Int,
    firstRegistrationId: Int,
    secondRegistrationId: Int,
    wasWalkOver: Boolean,
    winner: Int?
) : Match(id, competitionCategoryId, firstRegistrationId, secondRegistrationId, wasWalkOver, winner)

data class GameResult(
    val id: Int,
    val number: Int,
    val firstRegistrationResult: Int,
    val secondRegistrationResult: Int
)