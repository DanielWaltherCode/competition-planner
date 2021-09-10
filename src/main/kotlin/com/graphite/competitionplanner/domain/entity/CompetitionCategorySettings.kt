package com.graphite.competitionplanner.domain.entity

import com.graphite.competitionplanner.competitioncategory.domain.interfaces.GameSettingsDTO
import com.graphite.competitionplanner.competitioncategory.domain.interfaces.GeneralSettingsDTO

sealed class CompetitionCategorySettings

class GeneralSettings(
    val cost: Float, // TODO: Maybe change underlying representation of cost to string / big integer to avoid rounding problems
    val drawTypeId: DrawType,
    val playersPerGroup: Int,
    val playersToPlayOff: Int,
    val poolDrawStrategy: PoolDrawStrategy
) : CompetitionCategorySettings() {
    init {
        require(cost >= 0f) { "Cost must be greater or equal than zero." }
        require(playersPerGroup >= 2) { "There must be at least two players per group." }
        require(playersToPlayOff >= 1) { "At least one player must advance to play-off" }
        require(playersPerGroup >= playersToPlayOff) { "There cannot be more player going to play-off than there are player in the group." }
    }

    constructor(dto: GeneralSettingsDTO) : this(
        dto.cost,
        DrawType.valueOf(dto.drawType.name),
        dto.playersPerGroup,
        dto.playersToPlayOff,
        PoolDrawStrategy.valueOf(dto.poolDrawStrategy.name)
    )
}

class GameSettings(
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
) : CompetitionCategorySettings() {
    init {
        require(numberOfSets > 0) { "Number of sets must be greater than zero." }
        require(winScore > 0) { "Win score has to be greater than zero." }
        require(winMargin > 0) { "Win margin has to be greater than zero." }
        require(numberOfSetsFinal > 0) { "Number of sets in the final must be greater than zero." }
        require(winScoreFinal > 0) { "Win score in the finals must be greater than zero." }
        require(winMarginFinal > 0) { "Win margin in the final must be greater than zero." }
        require(winScoreTiebreak > 0) { "Win score when there is a tie break must be greater than zero." }
        require(winMarginTieBreak > 0) { "Win margin when there is a tie break must be greater than zero." }
    }

    constructor(dto: GameSettingsDTO) : this(
        dto.numberOfSets,
        dto.winScore,
        dto.winMargin,
        dto.differentNumberOfGamesFromRound,
        dto.numberOfSetsFinal,
        dto.winScoreFinal,
        dto.winMarginFinal,
        dto.tiebreakInFinalGame,
        dto.winScoreTiebreak,
        dto.winMarginTieBreak
    )
}