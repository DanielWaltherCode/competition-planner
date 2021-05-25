package com.graphite.competitionplanner.domain.usecase.player

import com.graphite.competitionplanner.domain.dto.PlayerDTO
import com.graphite.competitionplanner.domain.dto.PlayerEntityDTO
import com.graphite.competitionplanner.domain.entity.Player
import com.graphite.competitionplanner.domain.interfaces.IPlayerRepository
import com.graphite.competitionplanner.domain.usecase.club.FindClub
import org.springframework.stereotype.Component

@Component
class UpdatePlayer(
    val playerRepository: IPlayerRepository,
    val verifyClubExist: FindClub
) {
    fun execute(dto: PlayerDTO): PlayerEntityDTO {
        val club = verifyClubExist.byId(dto.clubId)
        val player = PlayerEntityDTO(dto, club)
        Player(player) // Verifying
        playerRepository.update(dto)
        return player
    }
}