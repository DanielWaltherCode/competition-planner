package com.graphite.competitionplanner.player.service

import com.fasterxml.jackson.annotation.JsonFormat
import com.graphite.competitionplanner.club.interfaces.ClubNoAddressDTO
import com.graphite.competitionplanner.player.domain.*
import com.graphite.competitionplanner.player.domain.interfaces.NewPlayerDTO
import com.graphite.competitionplanner.player.domain.interfaces.PlayerWithClubDTO
import com.graphite.competitionplanner.player.repository.PlayerRepository
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

    fun getPlayersByClubId(clubId: Int): List<PlayerWithClubDTO> {
        return listAllPlayersInClub.execute(clubId)
    }

    fun addPlayer(player: NewPlayerDTO): PlayerWithClubDTO {
        return createPlayer.execute(player)
    }

    fun updatePlayer(player: com.graphite.competitionplanner.player.domain.interfaces.PlayerDTO): PlayerWithClubDTO {
        return updatePlayer.execute(player)
    }

    fun getPlayer(playerId: Int): PlayerWithClubDTO {
        return findPlayer.byId(playerId)
    }

    fun findByName(partOfName: String): List<PlayerWithClubDTO> {
        return findPlayer.byPartName(partOfName)
    }

    fun deletePlayer(playerId: Int): Boolean {
        deletePlayer.execute(
            com.graphite.competitionplanner.player.domain.interfaces.PlayerDTO(
                playerId,
                "",
                "",
                0,
                LocalDate.now()
            )
        )
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

@Deprecated("Use Domain.dto.PlayerDTO instead")
data class PlayerDTO(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val club: ClubNoAddressDTO,
    @JsonFormat(pattern = "yyyy-MM-dd")
    val dateOfBirth: LocalDate
)
