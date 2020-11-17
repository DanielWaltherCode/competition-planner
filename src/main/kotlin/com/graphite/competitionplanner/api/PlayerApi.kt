package com.graphite.competitionplanner.api

import com.graphite.competitionplanner.repositories.PlayerRepository
import com.graphite.competitionplanner.service.PlayerDTO
import com.graphite.competitionplanner.service.PlayerService
import com.graphite.competitionplanner.tables.pojos.Player
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDate
import javax.validation.Valid
import javax.validation.constraints.NotNull
import kotlin.streams.toList


@RestController
@RequestMapping("/player")
class PlayerApi(
        val playerRepository: PlayerRepository,
        val playerService: PlayerService) {

    @PostMapping
    fun addPlayer(@Valid @RequestBody playerDTO: PlayerDTO): PlayerDTO {
        return playerService.addPlayer(playerDTO)
    }

    @PutMapping
    fun updatePlayer(@Valid @RequestBody playerDTO: PlayerDTO): PlayerDTO {
        return playerService.updatePlayer(playerDTO)
    }

    @GetMapping("/{playerId}")
    fun getPlayer(@PathVariable playerId: Int): PlayerDTO {
        return playerService.getPlayer(playerId)
    }

    @GetMapping("name-search")
    fun searchByName(@RequestParam partOfName: String): List<PlayerDTO> {
        return playerService.findByName(partOfName)
    }

    @GetMapping
    fun getPlayersByClubId(@RequestParam clubId: Int): List<PlayerDTO> {
       return playerService.getPlayersByClubId(clubId)
    }

    @DeleteMapping("/{playerId}")
    fun deletePlayer(@PathVariable playerId: Int): Boolean {
        return playerRepository.deletePlayer(playerId)
    }


}

