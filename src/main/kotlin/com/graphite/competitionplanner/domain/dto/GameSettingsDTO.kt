package com.graphite.competitionplanner.domain.dto

import com.graphite.competitionplanner.domain.entity.GameSettings

data class GameSettingsDTO(
    val numberOfSets: Int,
    val winScore: Int,
    val winMargin: Int,
    val numberOfSetsFinal: Int,
    val winScoreFinal: Int,
    val winMarginFinal: Int,
    val winScoreTiebreak: Int,
    val winMarginTieBreak: Int
) {
    constructor(entity: GameSettings) : this(
        entity.numberOfSets,
        entity.winScore,
        entity.winMargin,
        entity.numberOfSetsFinal,
        entity.winScoreFinal,
        entity.winMarginFinal,
        entity.winScoreTiebreak,
        entity.winMarginTieBreak
    )
}