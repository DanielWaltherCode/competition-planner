package com.graphite.competitionplanner.domain.usecase.player

import com.graphite.competitionplanner.domain.dto.NewPlayerDTO
import com.graphite.competitionplanner.domain.dto.PlayerWithClubDTO
import com.graphite.competitionplanner.domain.entity.Player
import com.graphite.competitionplanner.domain.interfaces.IPlayerRepository
import com.graphite.competitionplanner.domain.usecase.club.FindClub
import org.springframework.stereotype.Component

@Component
class CreatePlayer(
    val playerRepository: IPlayerRepository,
    val findClub: FindClub
) {
    fun execute(dto: NewPlayerDTO): PlayerWithClubDTO {
        val clubDto = findClub.byId(dto.clubId)
        Player(0, dto.firstName, dto.lastName, dto.dateOfBirth) // Verifying
        val player = playerRepository.store(dto)
        return PlayerWithClubDTO(player, clubDto)
    }
}