package com.graphite.competitionplanner.player.api

import com.graphite.competitionplanner.competition.domain.FindCompetitions
import com.graphite.competitionplanner.player.domain.*
import com.graphite.competitionplanner.player.interfaces.PlayerSpec
import com.graphite.competitionplanner.player.interfaces.PlayerWithClubDTO
import com.graphite.competitionplanner.registration.service.PlayerCompetitionDTO
import com.graphite.competitionplanner.registration.service.RegistrationService
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("api/player")
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

    @PostMapping("/competition/{competitionId}")
    fun addPlayerInCompetition(@PathVariable competitionId: Int, @Valid @RequestBody playerSpec: PlayerSpec): PlayerWithClubDTO {
        return createPlayer.executeForCompetition(competitionId, playerSpec)
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

    @GetMapping("name-search/with-competition/{competitionId}")
    fun searchByPartOfName(@PathVariable competitionId: Int, @RequestParam partOfName: String): List<PlayerWithClubDTO> {
        return findPlayer.byPartNameWithCompetition(competitionId, partOfName)
    }

    @GetMapping("name-search/{competitionId}")
    fun searchPlayerInCompetition(@PathVariable competitionId: Int, @RequestParam partOfName: String): List<PlayerWithClubDTO> {
        val competition = findCompetitions.byId(competitionId)
        return findPlayer.byPartNameInCompetition(partOfName, competition)
    }

    @GetMapping
    fun getPlayersByClubId(@RequestParam clubId: Int): List<PlayerWithClubDTO> {
        val players = listAllPlayersInClub.execute(clubId)
        return players.sortedBy { it.lastName }
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
