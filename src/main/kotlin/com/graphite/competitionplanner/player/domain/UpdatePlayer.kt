package com.graphite.competitionplanner.player.domain

import com.graphite.competitionplanner.domain.dto.PlayerDTO
import com.graphite.competitionplanner.domain.dto.PlayerWithClubDTO
import com.graphite.competitionplanner.domain.entity.Player
import com.graphite.competitionplanner.player.domain.interfaces.IPlayerRepository
import com.graphite.competitionplanner.club.domain.FindClub
import org.springframework.stereotype.Component

@Component
class UpdatePlayer(
    val playerRepository: IPlayerRepository,
    val verifyClubExist: FindClub
) {
    fun execute(dto: PlayerDTO): PlayerWithClubDTO {
        val club = verifyClubExist.byId(dto.clubId)
        val player = PlayerWithClubDTO(dto, club)
        Player(dto) // Verifying
        playerRepository.update(dto)
        return player
    }
}