package com.graphite.competitionplanner.domain.usecase.player

import com.graphite.competitionplanner.domain.dto.PlayerDTO
import com.graphite.competitionplanner.domain.interfaces.IPlayerRepository
import org.springframework.stereotype.Component

@Component
class DeletePlayer(
    val playerRepository: IPlayerRepository
) {
    fun execute(dto: PlayerDTO): PlayerDTO {
        return playerRepository.delete(dto)
    }
}