package com.graphite.competitionplanner.player.domain

import com.graphite.competitionplanner.club.domain.FindClub
import com.graphite.competitionplanner.player.interfaces.IPlayerRepository
import com.graphite.competitionplanner.player.interfaces.PlayerSpec
import com.graphite.competitionplanner.player.interfaces.PlayerWithClubDTO
import org.springframework.stereotype.Component

@Component
class CreatePlayer(
    val playerRepository: IPlayerRepository,
    val findClub: FindClub
) {
    fun execute(dto: PlayerSpec): PlayerWithClubDTO {
        val clubDto = findClub.byId(dto.clubId)
        val player = playerRepository.store(dto)
        return PlayerWithClubDTO(player, clubDto)
    }
}