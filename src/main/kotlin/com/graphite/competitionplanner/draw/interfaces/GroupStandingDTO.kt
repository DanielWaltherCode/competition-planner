package com.graphite.competitionplanner.draw.interfaces

import com.graphite.competitionplanner.player.interfaces.PlayerWithClubDTO

data class GroupStandingDTO(
    val player: List<PlayerWithClubDTO>,
    val groupPosition: Int,
    val matchesWon: Int,
    val matchesPlayed: Int,
    val matchesWonLost: WonLostDTO,
    val gamesWonLost: WonLostDTO,
    val pointsWonLost: WonLostDTO,
    val groupScore: Int,
    val subgroupStanding: GroupStandingDTO?,
    var sortedBy: SortedBy?
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
    val gameQuotient: Float,
    val pointQuotient: Float,
    val groupScore: Int, // 2 point for a win, 1 for a loss, 0 if the player gave WO/withdrew
    var subgroupStanding: GroupStandingWithRegistrationId?,
    var sortedBy: SortedBy
)

enum class SortedBy{
    ORIGINAL, MATCH_SCORE, GAME_QUOTIENT, POINT_QUOTIENT, DRAW
}