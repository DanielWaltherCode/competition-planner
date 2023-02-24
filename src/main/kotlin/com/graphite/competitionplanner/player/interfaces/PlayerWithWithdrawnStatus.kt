package com.graphite.competitionplanner.player.interfaces

data class PlayerWithWithdrawnStatus(
    val player: PlayerWithClubDTO,
    val hasWithdrawn: Boolean
)