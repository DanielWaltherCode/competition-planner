package com.graphite.competitionplanner.service

import com.graphite.competitionplanner.api.ClubNoAddressDTO
import com.graphite.competitionplanner.domain.dto.NewPlayerDTO
import com.graphite.competitionplanner.domain.dto.PlayerEntityDTO
import com.graphite.competitionplanner.domain.usecase.player.*
import com.graphite.competitionplanner.repositories.PlayerRepository
import com.graphite.competitionplanner.tables.Club.CLUB
import com.graphite.competitionplanner.tables.Player.PLAYER
import com.graphite.competitionplanner.tables.records.ClubRecord
import com.graphite.competitionplanner.tables.records.PlayerRecord
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDate

@Service
class PlayerService(
    val playerRepository: PlayerRepository,
    val createPlayer: CreatePlayer,
    val updatePlayer: UpdatePlayer,
    val listAllPlayersInClub: ListAllPlayersInClub,
    val deletePlayer: DeletePlayer,
    val findPlayer: FindPlayer
) {

    fun getPlayersByClubId(clubId: Int): List<PlayerEntityDTO> {
        return listAllPlayersInClub.execute(clubId)
    }

    fun addPlayer(player: NewPlayerDTO): PlayerEntityDTO {
        return createPlayer.execute(player)
    }

    fun updatePlayer(player: com.graphite.competitionplanner.domain.dto.PlayerDTO): PlayerEntityDTO {
        return updatePlayer.execute(player)
    }

    fun getPlayer(playerId: Int): PlayerEntityDTO {
        return findPlayer.byId(playerId)
    }

    fun findByName(partOfName: String): List<PlayerEntityDTO> {
        return findPlayer.byPartName(partOfName)
    }

    fun deletePlayer(playerId: Int): Boolean {
        deletePlayer.execute(com.graphite.competitionplanner.domain.dto.PlayerDTO(playerId, "", "", 0, LocalDate.now()))
        return true
    }

    @Deprecated("Use getPlayer instead")
    fun getPlayerLegacy(playerId: Int): PlayerDTO {
        val record = playerRepository.getPlayer(playerId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "No player with id $playerId found")

        val playerRecord = record.into(PLAYER)
        val clubRecord = record.into(CLUB)
        return recordsToDTO(playerRecord, clubRecord)
    }

    private fun recordsToDTO(player: PlayerRecord, club: ClubRecord): PlayerDTO {
        return PlayerDTO(
            player.id,
            player.firstName,
            player.lastName,
            ClubNoAddressDTO(club.id, club.name),
            player.dateOfBirth
        )
    }
}

@Deprecated("Use Domain.dto.PlayeDTO instead")
data class PlayerDTO(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val club: ClubNoAddressDTO,
    val dateOfBirth: LocalDate
)
