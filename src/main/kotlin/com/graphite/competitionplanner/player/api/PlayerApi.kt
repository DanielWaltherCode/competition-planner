package com.graphite.competitionplanner.player.api

import com.graphite.competitionplanner.competition.domain.FindCompetitions
import com.graphite.competitionplanner.player.domain.*
import com.graphite.competitionplanner.player.interfaces.PlayerSpec
import com.graphite.competitionplanner.player.interfaces.PlayerWithClubDTO
import com.graphite.competitionplanner.registration.service.PlayerCompetitionDTO
import com.graphite.competitionplanner.registration.service.RegistrationService
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/player")
class PlayerApi(
    val listAllPlayersInClub: ListAllPlayersInClub,
    val createPlayer: CreatePlayer,
    val updatePlayer: UpdatePlayer,
    val findPlayer: FindPlayer,
    val deletePlayer: DeletePlayer,
    val findCompetitions: FindCompetitions,
    val registrationService: RegistrationService
) {
    @PostMapping
    fun addPlayer(@Valid @RequestBody playerSpec: PlayerSpec): PlayerWithClubDTO {
        return createPlayer.execute(playerSpec)
    }

    @PutMapping("/{playerId}")
    fun updatePlayer(@PathVariable playerId: Int, @Valid @RequestBody playerSpec: PlayerSpec): PlayerWithClubDTO {
        return updatePlayer.execute(playerId, playerSpec)
    }

    @GetMapping("/{playerId}")
    fun getPlayer(@PathVariable playerId: Int): PlayerWithClubDTO {
        return findPlayer.byId(playerId)
    }

    @GetMapping("name-search")
    fun searchByPartOfName(@RequestParam partOfName: String): List<PlayerWithClubDTO> {
        return findPlayer.byPartName(partOfName)
    }

    @GetMapping("name-search/{competitionId}")
    fun searchPlayerInCompetition(@PathVariable competitionId: Int, @RequestParam partOfName: String): List<PlayerWithClubDTO> {
        val competition = findCompetitions.byId(competitionId)
        return findPlayer.byPartNameInCompetition(partOfName, competition)
    }

    @GetMapping
    fun getPlayersByClubId(@RequestParam clubId: Int): List<PlayerWithClubDTO> {
        return listAllPlayersInClub.execute(clubId)
    }

    @DeleteMapping("/{playerId}")
    fun deletePlayer(@PathVariable playerId: Int): Boolean {
        deletePlayer.execute(playerId)
        return true
    }

    @GetMapping("/player/{playerId}/registration")
    fun getRegistrationByPlayerId(@PathVariable playerId: Int): PlayerCompetitionDTO {
        return registrationService.getRegistrationByPlayerId(playerId)
    }
}
