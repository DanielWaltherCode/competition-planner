package com.graphite.competitionplanner.domain.entity

sealed class CompetitionCategorySettings

// TODO: Add validation
class GeneralSettings(
    val cost: Float,
    val drawTypeId: DrawType,
    val playersPerGroup: Int,
    val playersToPlayOff: Int,
    val poolDrawStrategy: PoolDrawStrategy
) : CompetitionCategorySettings()

// TODO: Add validation
class GameSettings(
    val numberOfSets: Int,
    val winScore: Int,
    val winMargin: Int,
    val numberOfSetsFinal: Int,
    val winScoreFinal: Int,
    val winMarginFinal: Int,
    val winScoreTiebreak: Int,
    val winMarginTieBreak: Int
) : CompetitionCategorySettings()