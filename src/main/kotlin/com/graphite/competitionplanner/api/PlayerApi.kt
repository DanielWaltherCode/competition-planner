package com.graphite.competitionplanner.api

import com.graphite.competitionplanner.domain.dto.NewPlayerDTO
import com.graphite.competitionplanner.domain.dto.PlayerWithClubDTO
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
    fun addPlayer(@Valid @RequestBody playerSpec: PlayerSpec): PlayerWithClubDTO {
        val newPlayerDto = NewPlayerDTO(
            playerSpec.firstName,
            playerSpec.lastName,
            playerSpec.clubId,
            playerSpec.dateOfBirth
        )
        return playerService.addPlayer(newPlayerDto)
    }

    @PutMapping("/{playerId}")
    fun updatePlayer(@PathVariable playerId: Int, @Valid @RequestBody playerSpec: PlayerSpec): PlayerWithClubDTO {
        val playerDto = com.graphite.competitionplanner.domain.dto.PlayerDTO(
            playerId,
            playerSpec.firstName,
            playerSpec.lastName,
            playerSpec.clubId,
            playerSpec.dateOfBirth
        )
        return playerService.updatePlayer(playerDto)
    }

    @GetMapping("/{playerId}")
    fun getPlayer(@PathVariable playerId: Int): PlayerWithClubDTO {
        return playerService.getPlayer(playerId)
    }

    @GetMapping("name-search")
    fun searchByPartOfName(@RequestParam partOfName: String): List<PlayerWithClubDTO> {
        return playerService.findByName(partOfName)
    }

    @GetMapping
    fun getPlayersByClubId(@RequestParam clubId: Int): List<PlayerWithClubDTO> {
        return playerService.getPlayersByClubId(clubId)
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
    val clubId: Int,
    val dateOfBirth: LocalDate
)