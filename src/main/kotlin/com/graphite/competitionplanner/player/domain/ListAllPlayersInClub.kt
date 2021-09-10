package com.graphite.competitionplanner.player.domain

import com.graphite.competitionplanner.domain.dto.PlayerWithClubDTO
import com.graphite.competitionplanner.player.domain.interfaces.IPlayerRepository
import com.graphite.competitionplanner.club.domain.FindClub
import org.springframework.stereotype.Component

@Component
class ListAllPlayersInClub(
    val playerRepository: IPlayerRepository,
    val findClub: FindClub
) {
    fun execute(clubId: Int): List<PlayerWithClubDTO> {
        val club = findClub.byId(clubId)
        return playerRepository.playersInClub(club)
    }
}