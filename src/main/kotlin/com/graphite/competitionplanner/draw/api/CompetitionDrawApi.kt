package com.graphite.competitionplanner.api.competition

import com.graphite.competitionplanner.club.interfaces.ClubDTO
import com.graphite.competitionplanner.domain.entity.Round
import com.graphite.competitionplanner.draw.service.*
import com.graphite.competitionplanner.match.service.MatchAndResultDTO
import com.graphite.competitionplanner.player.interfaces.PlayerDTO
import com.graphite.competitionplanner.player.interfaces.PlayerWithClubDTO
import com.graphite.competitionplanner.player.service.PlayerService
import com.graphite.competitionplanner.registration.service.CompetitionCategoryDTO
import com.graphite.competitionplanner.result.service.GameDTO
import com.graphite.competitionplanner.result.service.ResultDTO
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalUnit

@RestController
@RequestMapping("competition/{competitionId}/draw/{competitionCategoryId}")
class CompetitionDrawApi(val drawService: DrawService,
val playerService: PlayerService) {

    // Can be used both to create initial draw and to make a new draw if desired
    @PutMapping
    fun makeDraw(@PathVariable competitionCategoryId: Int): DrawDTO {
        return drawService.createDraw(competitionCategoryId)
    }

    @GetMapping
    fun getDraw(@PathVariable competitionCategoryId: Int): DrawDTO {
        return drawService.getDraw(competitionCategoryId)
    }

    @GetMapping("/pool")
    fun getPoolDraw(@PathVariable competitionCategoryId: Int) {
    }

    @GetMapping("/playoffs")
    fun getPlayoffDraw(@PathVariable competitionCategoryId: Int) {
    }

    @DeleteMapping
    fun deleteDraw(@PathVariable competitionCategoryId: Int) {
        return drawService.deleteDraw(competitionCategoryId)
    }

    @GetMapping("/is-draw-made")
    fun isDrawMade(@PathVariable competitionCategoryId: Int): Boolean {
        return drawService.isDrawMade(competitionCategoryId)
    }

    @GetMapping("mock")
    fun getMockPlayoffDraw(): List<PlayoffRoundDTO> {
        // Create matches for quarter finals, semifinals, leaving final empty
        // For test only
        val player1DTO = PlayerDTO(1, "Anders", "And", 1, LocalDate.now().minus(19, ChronoUnit.YEARS))
        val player1 = PlayerWithClubDTO(
            player1DTO,
            ClubDTO(1, "Lugi", "Lund")
        )
        val player2 = PlayerWithClubDTO(
            PlayerDTO(2, "Stina", "Nilsson", 2, LocalDate.now().minus(19, ChronoUnit.YEARS)),
            ClubDTO(2, "Bjärred", "Bjärred")
        )

        val playoffRoundList = mutableListOf<PlayoffRoundDTO>()
        val matches = mutableListOf<MatchAndResultDTO>()

        // Round of 16
        for (i in 1..8) {
            matches.add(MatchAndResultDTO(
                i,
                null,
                null,
                CompetitionCategoryDTO(1, "Herrar 1"),
                "SINGLES",
                listOf(player1),
                listOf(player2),
                1,
                Round.ROUND_OF_16.name,
                listOf(player1DTO),
                ResultDTO(listOf(GameDTO(
                    1,
                    1,
                    11,
                    9
                ),
                    GameDTO(
                        1,
                        2,
                        6,
                        11
                    ),
                    GameDTO(
                        1,
                        3,
                        11,
                        8
                    ),
                    GameDTO(
                        1,
                        4,
                        11,
                        9
                    )
                ))
            ))
        }

        // Quarter final
        for (i in 1..4) {
            matches.add(MatchAndResultDTO(
                i,
                null,
                null,
                CompetitionCategoryDTO(1, "Herrar 1"),
                "SINGLES",
                listOf(player1),
                listOf(player2),
                1,
                Round.QUARTER_FINAL.name,
                listOf(player1DTO),
                ResultDTO(listOf(GameDTO(
                    1,
                    1,
                    11,
                    9
                ),
                    GameDTO(
                        1,
                        2,
                        6,
                        11
                    ),
                    GameDTO(
                        1,
                        3,
                        11,
                        8
                    ),
                    GameDTO(
                        1,
                        4,
                        11,
                        9
                    )
                ))
            ))
        }
        // Semi final
        for (i in 1..2) {
            matches.add(MatchAndResultDTO(
                i,
                null,
                null,
                CompetitionCategoryDTO(1, "Herrar 1"),
                "SINGLES",
                listOf(player1),
                listOf(player2),
                1,
                Round.SEMI_FINAL.name,
                listOf(player1DTO),
                ResultDTO(listOf(GameDTO(
                    1,
                    1,
                    11,
                    9
                ),
                    GameDTO(
                        1,
                        2,
                        6,
                        11
                    ),
                    GameDTO(
                        1,
                        3,
                        11,
                        8
                    ),
                    GameDTO(
                        1,
                        4,
                        11,
                        9
                    )
                ))
            ))
        }

        matches.add(
            MatchAndResultDTO(
                1,
                null,
                null,
                CompetitionCategoryDTO(1, "Herrar 1"),
                "SINGLES",
                listOf(playerService.getPlayer(0)),
                listOf(playerService.getPlayer(0)),
                1,
                Round.FINAL.name,
                listOf(player1DTO),
                ResultDTO(emptyList())
            ))

        val distinctRounds = matches.map { it.groupOrRound }.distinct()

        for (round in distinctRounds) {
            playoffRoundList.add(
                PlayoffRoundDTO(Round.valueOf(round),
                    matches.filter { it.groupOrRound == round }
                )
            )
        }
        return playoffRoundList
    }

}

data class DrawDTO(
    val groupDraw: GroupDrawDTO,
    val playOff: PlayoffDTO
)