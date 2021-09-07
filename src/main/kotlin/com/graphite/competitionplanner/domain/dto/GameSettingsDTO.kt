package com.graphite.competitionplanner.domain.dto

import com.graphite.competitionplanner.domain.entity.GameSettings
import com.graphite.competitionplanner.domain.entity.Round

data class GameSettingsDTO(
    val numberOfSets: Int,
    val winScore: Int,
    val winMargin: Int,
    val differentNumberOfGamesFromRound: Round,
    val numberOfSetsFinal: Int,
    val winScoreFinal: Int,
    val winMarginFinal: Int,
    val tiebreakInFinalGame: Boolean,
    val winScoreTiebreak: Int,
    val winMarginTieBreak: Int
) {
    constructor(entity: GameSettings) : this(
        entity.numberOfSets,
        entity.winScore,
        entity.winMargin,
        entity.differentNumberOfGamesFromRound,
        entity.numberOfSetsFinal,
        entity.winScoreFinal,
        entity.winMarginFinal,
        entity.tiebreakInFinalGame,
        entity.winScoreTiebreak,
        entity.winMarginTieBreak
    )
}