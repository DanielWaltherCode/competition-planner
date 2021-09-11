package com.graphite.competitionplanner.player.domain

import com.graphite.competitionplanner.club.domain.FindClub
import com.graphite.competitionplanner.player.interfaces.IPlayerRepository
import com.graphite.competitionplanner.player.interfaces.PlayerSpec
import com.graphite.competitionplanner.player.interfaces.PlayerWithClubDTO
import org.springframework.stereotype.Component

@Component
class UpdatePlayer(
    val playerRepository: IPlayerRepository,
    val verifyClubExist: FindClub
) {
    fun execute(playerId: Int, dto: PlayerSpec): PlayerWithClubDTO {
        val club = verifyClubExist.byId(dto.clubId)
        val player = PlayerWithClubDTO(playerId, dto.firstName, dto.lastName, club, dto.dateOfBirth)
        playerRepository.update(playerId, dto)
        return player
    }
}