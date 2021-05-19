package com.graphite.competitionplanner.api

import com.graphite.competitionplanner.domain.dto.NewPlayerDTO
import com.graphite.competitionplanner.service.*
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import javax.validation.Valid

@RestController
@RequestMapping("/player")
class PlayerApi(
    val playerService: PlayerService
) {

    @PostMapping
    fun addPlayer(@Valid @RequestBody playerSpec: PlayerSpec): PlayerDTO {
        val newPlayerDto = NewPlayerDTO(
            playerSpec.firstName,
            playerSpec.lastName,
            playerSpec.club.id,
            playerSpec.dateOfBirth
        )
        val createdPlayer = playerService.addPlayer(newPlayerDto)
        return PlayerDTO(
            createdPlayer.id,
            createdPlayer.firstName,
            createdPlayer.lastName,
            ClubNoAddressDTO(createdPlayer.club.id, playerSpec.club.name),
            createdPlayer.dateOfBirth
        )
    }

    @PutMapping("/{playerId}")
    fun updatePlayer(@PathVariable playerId: Int, @Valid @RequestBody playerSpec: PlayerSpec): PlayerDTO {
        val playerDto = com.graphite.competitionplanner.domain.dto.PlayerDTO(
            playerId,
            playerSpec.firstName,
            playerSpec.lastName,
            playerSpec.club.id,
            playerSpec.dateOfBirth
        )
        val updatedPlayer = playerService.updatePlayer(playerDto)

        return PlayerDTO(
            updatedPlayer.id,
            updatedPlayer.firstName,
            updatedPlayer.lastName,
            ClubNoAddressDTO(updatedPlayer.club.id, updatedPlayer.club.name),
            updatedPlayer.dateOfBirth
        )
    }

    @GetMapping("/{playerId}")
    fun getPlayer(@PathVariable playerId: Int): PlayerDTO {
        val found = playerService.getPlayerLegacy(playerId)
        return PlayerDTO(
            found.id,
            found.firstName,
            found.lastName,
            ClubNoAddressDTO(found.club.id, found.club.name),
            found.dateOfBirth
        )
    }

    @GetMapping("name-search")
    fun searchByPartOfName(@RequestParam partOfName: String): List<PlayerDTO> {
        val foundPlayers = playerService.findByName(partOfName)
        return foundPlayers.map {
            PlayerDTO(
                it.id,
                it.firstName,
                it.lastName,
                ClubNoAddressDTO(it.club.id, it.club.name),
                it.dateOfBirth
            )
        }
    }

    @GetMapping
    fun getPlayersByClubId(@RequestParam clubId: Int): List<PlayerDTO> {
        val foundPlayers = playerService.getPlayersByClubId(clubId)
        return foundPlayers.map {
            PlayerDTO(
                it.id,
                it.firstName,
                it.lastName,
                ClubNoAddressDTO(it.club.id, it.club.name),
                it.dateOfBirth
            )
        }
    }

    @DeleteMapping("/{playerId}")
    fun deletePlayer(@PathVariable playerId: Int): Boolean {
        return playerService.deletePlayer(playerId)
    }

}

@RestController
@RequestMapping("/player/{playerId}/registration")
class PlayerRegistrationApi(val registrationService: RegistrationService) {

    @GetMapping("/player/{playerId}")
    fun getByPlayerId(@PathVariable playerId: Int): PlayerCompetitionDTO {
        return registrationService.getRegistrationByPlayerId(playerId)
    }
}

data class PlayerSpec(
    val firstName: String,
    val lastName: String,
    val club: ClubNoAddressDTO,
    val dateOfBirth: LocalDate
)