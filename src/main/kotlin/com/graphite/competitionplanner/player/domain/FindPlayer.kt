package com.graphite.competitionplanner.player.domain

import com.graphite.competitionplanner.domain.dto.PlayerWithClubDTO
import com.graphite.competitionplanner.player.domain.interfaces.IPlayerRepository
import com.graphite.competitionplanner.club.domain.FindClub
import org.springframework.stereotype.Component

@Component
class FindPlayer(
    val playerRepository: IPlayerRepository,
    val findClub: FindClub
) {
    fun byId(id: Int): PlayerWithClubDTO {
        val player = playerRepository.findById(id)
        val club = findClub.byId(player.clubId)
        return PlayerWithClubDTO(player.id, player.firstName, player.lastName, club, player.dateOfBirth)
    }

    fun byPartName(partName: String): List<PlayerWithClubDTO> {
        return playerRepository.findByName(partName)
    }
}