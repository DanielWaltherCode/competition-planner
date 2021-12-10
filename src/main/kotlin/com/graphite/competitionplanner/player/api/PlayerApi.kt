package com.graphite.competitionplanner.player.api

import com.graphite.competitionplanner.player.domain.ListAllPlayersInClub
import com.graphite.competitionplanner.player.interfaces.PlayerSpec
import com.graphite.competitionplanner.player.interfaces.PlayerWithClubDTO
import com.graphite.competitionplanner.player.service.PlayerService
import com.graphite.competitionplanner.registration.service.PlayerCompetitionDTO
import com.graphite.competitionplanner.registration.service.RegistrationService
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/player")
class PlayerApi(
    val playerService: PlayerService,
    val listAllPlayersInClub: ListAllPlayersInClub
) {
    @PostMapping
    fun addPlayer(@Valid @RequestBody playerSpec: PlayerSpec): PlayerWithClubDTO {
        return playerService.addPlayer(playerSpec)
    }

    @PutMapping("/{playerId}")
    fun updatePlayer(@PathVariable playerId: Int, @Valid @RequestBody playerSpec: PlayerSpec): PlayerWithClubDTO {
        return playerService.updatePlayer(playerId, playerSpec)
    }

    @GetMapping("/{playerId}")
    fun getPlayer(@PathVariable playerId: Int): PlayerWithClubDTO {
        return playerService.getPlayer(playerId)
    }

    @GetMapping("name-search")
    fun searchByPartOfName(@RequestParam partOfName: String): List<PlayerWithClubDTO> {
        return playerService.findByName(partOfName)
    }

    @GetMapping("name-search/{competitionId}")
    fun searchPlayerInCompetition(@PathVariable competitionId: Int, @RequestParam partOfName: String): List<PlayerWithClubDTO> {
        return playerService.findByNameInCompetition(partOfName, competitionId)
    }

    @GetMapping
    fun getPlayersByClubId(@RequestParam clubId: Int): List<PlayerWithClubDTO> {
        return listAllPlayersInClub.execute(clubId)
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
