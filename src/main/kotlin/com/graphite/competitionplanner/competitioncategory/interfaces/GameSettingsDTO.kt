package com.graphite.competitionplanner.competitioncategory.interfaces

import com.graphite.competitionplanner.draw.interfaces.Round

data class GameSettingsDTO(
        val numberOfSets: Int,
        val winScore: Int,
        val winMargin: Int,
        val differentNumberOfGamesFromRound: Round,
        val numberOfSetsInPlayoff: Int,
        val winScoreInPlayoff: Int,
        val winMarginInPlayoff: Int,
        val tiebreakInFinalGame: Boolean,
        val winScoreTiebreak: Int,
        val winMarginTieBreak: Int,
        val useDifferentRulesInEndGame: Boolean
) {
    init {
        require(numberOfSets > 0) { "Number of sets must be greater than zero." }
        require(numberOfSets % 2 == 1) { "The number of sets has to be odd."}
        require(winScore > 0) { "Win score has to be greater than zero." }
        require(winMargin > 0) { "Win margin has to be greater than zero." }
        require(numberOfSetsInPlayoff > 0) { "Number of sets in the final must be greater than zero." }
        require(winScoreInPlayoff > 0) { "Win score in the finals must be greater than zero." }
        require(winMarginInPlayoff > 0) { "Win margin in the final must be greater than zero." }
        require(winScoreTiebreak > 0) { "Win score when there is a tie break must be greater than zero." }
        require(winMarginTieBreak > 0) { "Win margin when there is a tie break must be greater than zero." }
    }
}