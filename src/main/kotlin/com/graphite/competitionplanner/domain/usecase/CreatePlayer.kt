package com.graphite.competitionplanner.domain.usecase

import com.graphite.competitionplanner.domain.dto.PlayerDTO
import com.graphite.competitionplanner.domain.entity.Player
import com.graphite.competitionplanner.domain.interfaces.IPlayerRepository
import org.springframework.stereotype.Component

@Component
class CreatePlayer(val playerRepository: IPlayerRepository) {

    fun execute(dto: PlayerDTO): PlayerDTO {
        Player(dto) // Validating
        return playerRepository.store(dto);
    }
}