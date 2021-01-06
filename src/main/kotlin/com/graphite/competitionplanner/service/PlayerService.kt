package com.graphite.competitionplanner.service

import com.graphite.competitionplanner.api.ClubNoAddressDTO
import com.graphite.competitionplanner.api.PlayerSpec
import com.graphite.competitionplanner.repositories.ClubRepository
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
class PlayerService(val playerRepository: PlayerRepository, val clubRepository: ClubRepository) {

    fun getPlayersByClubId(clubId: Int): List<PlayerDTO> {
        val records = playerRepository.getPlayersByClub(clubId)
        val playerDTOs = mutableListOf<PlayerDTO>()

        for(record in records) {
            val player = record.into(PLAYER)
            val club = record.into(CLUB)
            playerDTOs.add(recordsToDTO(player, club))
        }
        return playerDTOs
    }

    fun addPlayer(playerSpec: PlayerSpec): PlayerDTO {
        val player = playerRepository.addPlayer(playerSpec)
        val club = clubRepository.getById(player.clubId)
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "No club with id ${player.clubId} found")
        return recordsToDTO(player, club)
    }

    fun updatePlayer(playerId: Int, playerSpec: PlayerSpec): PlayerDTO {
        val player = playerRepository.updatePlayer(playerId, playerSpec)
        val club = clubRepository.getById(player.clubId)
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "No club with id ${player.clubId} found")
        return recordsToDTO(player, club)
    }

    fun getPlayer(playerId: Int): PlayerDTO {
        val record = playerRepository.getPlayer(playerId)
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "No player with id $playerId found")

        val playerRecord = record.into(PLAYER)
        val clubRecord = record.into(CLUB)
        return recordsToDTO(playerRecord, clubRecord)
    }

    fun findByName(partOfName: String): List<PlayerDTO> {
        val records = playerRepository.findPlayersByPartOfName(partOfName)
        val playerDTOs = mutableListOf<PlayerDTO>()
        for (record in records) {
            val player = record.into(PLAYER)
            val club = record.into(CLUB)
            playerDTOs.add(recordsToDTO(player, club))
        }
        return playerDTOs
    }

}

data class PlayerDTO(
        val id: Int,
        val firstName: String,
        val lastName: String,
        val club: ClubNoAddressDTO,
        val dateOfBirth: LocalDate
)

fun recordsToDTO(player: PlayerRecord, club: ClubRecord): PlayerDTO {
    return PlayerDTO(player.id, player.firstName, player.lastName, ClubNoAddressDTO(club.id, club.name), player.dateOfBirth)
}
