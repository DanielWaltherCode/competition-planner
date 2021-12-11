package com.graphite.competitionplanner.competitioncategory.interfaces

data class GeneralSettingsDTO(
    val cost: Float,
    val drawType: DrawType,
    val playersPerGroup: Int,
    val playersToPlayOff: Int,
    val poolDrawStrategy: PoolDrawStrategy
) {
    init {
        require(cost >= 0f) { "Cost must be greater or equal than zero." }
        require(playersPerGroup >= 2) { "There must be at least two players per group." }
        require(playersToPlayOff >= 1) { "At least one player must advance to play-off" }
        require(playersPerGroup >= playersToPlayOff) { "There cannot be more player going to play-off than there are player in the group." }
    }
}