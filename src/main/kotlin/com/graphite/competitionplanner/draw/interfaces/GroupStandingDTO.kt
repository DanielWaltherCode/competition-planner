package com.graphite.competitionplanner.draw.interfaces

import com.graphite.competitionplanner.player.interfaces.PlayerWithClubDTO

data class GroupStandingDTO(
    val player: List<PlayerWithClubDTO>,
    val matchesWon: Int,
    val matchesPlayed: Int,
    val matchesWonLost: WonLostDTO,
    val gamesWonLost: WonLostDTO,
    val pointsWonLost: WonLostDTO
)

data class WonLostDTO(
    val won: Int,
    val lost: Int,
)

