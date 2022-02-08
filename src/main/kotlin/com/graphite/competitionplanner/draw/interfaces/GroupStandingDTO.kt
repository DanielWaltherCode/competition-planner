package com.graphite.competitionplanner.draw.interfaces

import com.graphite.competitionplanner.player.interfaces.PlayerWithClubDTO

data class GroupStandingDTO(
    val player: List<PlayerWithClubDTO>,
    val groupPosition: Int,
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

data class GroupStandingWithRegistrationId(
    val registrationId: Int,
    val matchesWon: Int,
    val matchesPlayed: Int,
    val matchesWonLost: WonLostDTO,
    val gamesWonLost: WonLostDTO,
    val pointsWonLost: WonLostDTO,
    val matchDifference: Int,
    val gameDifference: Int,
    val pointDifference: Int,
    val groupScore: Int // 2 point for a win, 1 for a loss, 0 if the player gave WO/withdrew

)

