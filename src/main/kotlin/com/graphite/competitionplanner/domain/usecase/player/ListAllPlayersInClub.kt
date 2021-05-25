package com.graphite.competitionplanner.domain.usecase.player

import com.graphite.competitionplanner.domain.dto.PlayerEntityDTO
import com.graphite.competitionplanner.domain.interfaces.IPlayerRepository
import com.graphite.competitionplanner.domain.usecase.club.FindClub
import org.springframework.stereotype.Component

@Component
class ListAllPlayersInClub(
    val playerRepository: IPlayerRepository,
    val findClub: FindClub
) {
    fun execute(clubId: Int): List<PlayerEntityDTO> {
        val club = findClub.byId(clubId)
        return playerRepository.playersInClub(club)
    }
}