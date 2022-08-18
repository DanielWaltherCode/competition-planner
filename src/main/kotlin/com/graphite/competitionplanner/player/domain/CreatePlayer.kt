package com.graphite.competitionplanner.player.domain

import com.graphite.competitionplanner.category.interfaces.CategoryType
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
    fun execute(playerSpec: PlayerSpec): PlayerWithClubDTO {
        val clubDto = findClub.byId(playerSpec.clubId)
        // TODO: This has to be a transaction. Ranking has to be stored together with player.
        val player = playerRepository.store(playerSpec)
        playerRepository.addPlayerRanking(playerId = player.id,
            rankToAdd = 0,
            categoryType = CategoryType.SINGLES.toString())
        return PlayerWithClubDTO(player, clubDto)
    }
}