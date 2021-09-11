package com.graphite.competitionplanner.player.domain

import com.graphite.competitionplanner.player.domain.interfaces.IPlayerRepository
import com.graphite.competitionplanner.player.domain.interfaces.PlayerWithClubDTO
import org.springframework.stereotype.Component

@Component
class ListAllPlayersInClub(
    val playerRepository: IPlayerRepository
) {
    fun execute(clubId: Int): List<PlayerWithClubDTO> {
        return playerRepository.playersInClub(clubId)
    }
}