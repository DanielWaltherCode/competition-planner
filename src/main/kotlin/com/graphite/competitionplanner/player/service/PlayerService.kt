package com.graphite.competitionplanner.player.service

import com.graphite.competitionplanner.player.domain.*
import com.graphite.competitionplanner.player.interfaces.PlayerSpec
import com.graphite.competitionplanner.player.interfaces.PlayerWithClubDTO
import com.graphite.competitionplanner.registration.repository.RegistrationRepository
import org.springframework.stereotype.Service

@Service
class PlayerService(
    val createPlayer: CreatePlayer,
    val updatePlayer: UpdatePlayer,
    val listAllPlayersInClub: ListAllPlayersInClub,
    val deletePlayer: DeletePlayer,
    val findPlayer: FindPlayer,
    val registrationRepository: RegistrationRepository
) {

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

    fun findByNameInCompetition(partOfName: String, competitionId: Int): List<PlayerWithClubDTO> {
        val players = registrationRepository.getRegistreredPlayersInCompetition(competitionId)
        val matchingPlayers = players.filter {
            it.firstName.startsWith(partOfName, ignoreCase = true)
                    || it.lastName.startsWith(partOfName, ignoreCase = true)
        }.distinctBy { it.id }

        return matchingPlayers.map { getPlayer(it.id) }
    }

    fun deletePlayer(playerId: Int): Boolean {
        deletePlayer.execute(playerId)
        return true
    }
}