package com.graphite.competitionplanner.player.service

import com.graphite.competitionplanner.player.domain.*
import com.graphite.competitionplanner.player.interfaces.PlayerSpec
import com.graphite.competitionplanner.player.interfaces.PlayerWithClubDTO
import org.springframework.stereotype.Service

@Service
class PlayerService(
    val createPlayer: CreatePlayer,
    val updatePlayer: UpdatePlayer,
    val listAllPlayersInClub: ListAllPlayersInClub,
    val deletePlayer: DeletePlayer,
    val findPlayer: FindPlayer
) {

    fun getPlayersByClubId(clubId: Int): List<PlayerWithClubDTO> {
        return listAllPlayersInClub.execute(clubId)
    }

    fun addPlayer(player: PlayerSpec): PlayerWithClubDTO {
        return createPlayer.execute(player)
    }

    fun updatePlayer(id: Int, spec: PlayerSpec): PlayerWithClubDTO {
        return updatePlayer.execute(id, spec)
    }

    fun getPlayer(playerId: Int): PlayerWithClubDTO {
        return findPlayer.byId(playerId)
    }

    fun findByName(partOfName: String): List<PlayerWithClubDTO> {
        return findPlayer.byPartName(partOfName)
    }

    fun deletePlayer(playerId: Int): Boolean {
        deletePlayer.execute(playerId)
        return true
    }
}