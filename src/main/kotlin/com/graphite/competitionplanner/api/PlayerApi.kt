package com.graphite.competitionplanner.api

import com.graphite.competitionplanner.repositories.PlayerRepository
import com.graphite.competitionplanner.tables.pojos.Player
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate
import javax.validation.Valid
import javax.validation.constraints.NotNull


@RestController
@RequestMapping("/player")
class PlayerApi(
        val playerRepository: PlayerRepository
) {
    @PostMapping
    fun addPlayer(@Valid @RequestBody playerDTO: PlayerDTO): PlayerDTO {
        val playerPojo: Player = playerRepository.addPlayer(playerDTO)
        return PlayerDTO(playerPojo.id, playerPojo.firstName, playerPojo.lastName, playerPojo.clubId, playerPojo.dateOfBirth)
    }
}

data class PlayerDTO(
        val id: Int?,
        @NotNull
        val firstName: String,
        @NotNull
        val lastName: String,
        @NotNull
        val clubId: Int = 0,
        @NotNull
        val dateOfBirth: LocalDate
)