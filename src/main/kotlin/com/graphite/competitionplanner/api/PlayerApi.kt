package com.graphite.competitionplanner.api

import com.graphite.competitionplanner.service.*
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import javax.validation.Valid
import org.springframework.http.HttpStatus

import org.springframework.web.server.ResponseStatusException


@RestController
@RequestMapping("/player")
class PlayerApi(
        val playerService: PlayerService) {

    @PostMapping
    fun addPlayer(@Valid @RequestBody playerSpec: PlayerSpec): PlayerDTO {
        return playerService.addPlayer(playerSpec)
    }

    @PutMapping("/{playerId}")
    fun updatePlayer(@PathVariable playerId: Int, @Valid @RequestBody playerSpec: PlayerSpec): PlayerDTO {
        return playerService.updatePlayer(playerId, playerSpec)
    }

    @GetMapping("/{playerId}")
    fun getPlayer(@PathVariable playerId: Int): PlayerDTO {
        return playerService.getPlayer(playerId)
    }

    @GetMapping("name-search")
    fun searchByPartOfName(@RequestParam partOfName: String): List<PlayerDTO> {
        return playerService.findByName(partOfName)
    }

    @GetMapping
    fun getPlayersByClubId(@RequestParam clubId: Int): List<PlayerDTO> {
       return playerService.getPlayersByClubId(clubId)
    }

    @DeleteMapping("/{playerId}")
    fun deletePlayer(@PathVariable playerId: Int): Boolean {
        try {
            return playerService.deletePlayer(playerId)
        } catch (exception: PlayerNotFoundException) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, exception.message, exception)
        }
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