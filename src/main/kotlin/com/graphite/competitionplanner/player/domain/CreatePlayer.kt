package com.graphite.competitionplanner.player.domain

import com.graphite.competitionplanner.club.domain.FindClub
import com.graphite.competitionplanner.player.interfaces.IPlayerRepository
import com.graphite.competitionplanner.player.interfaces.PlayerRankingSpec
import com.graphite.competitionplanner.player.interfaces.PlayerSpec
import com.graphite.competitionplanner.player.interfaces.PlayerWithClubDTO
import org.springframework.stereotype.Component

@Component
class CreatePlayer(
    val playerRepository: IPlayerRepository,
    val findClub: FindClub
) {
    fun execute(playerSpec: PlayerSpec): PlayerWithClubDTO {
        val clubDto = findClub.byId(playerSpec.clubId)
        val rankingSpec = PlayerRankingSpec(0, 0)
        val player = playerRepository.store(playerSpec, rankingSpec)
        return PlayerWithClubDTO(player, clubDto)
    }
}