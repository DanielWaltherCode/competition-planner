package com.graphite.competitionplanner.player.domain

import com.graphite.competitionplanner.player.interfaces.IPlayerRepository
import com.graphite.competitionplanner.player.interfaces.PlayerDTO
import org.springframework.stereotype.Component

@Component
class DeletePlayer(
    val playerRepository: IPlayerRepository
) {
    fun execute(id: Int): PlayerDTO {
        return playerRepository.delete(id)
    }
}