package com.graphite.competitionplanner.draw.api

import com.graphite.competitionplanner.draw.domain.CreateDraw
import com.graphite.competitionplanner.draw.domain.GetDraw
import com.graphite.competitionplanner.draw.interfaces.CompetitionCategoryDrawDTO
import com.graphite.competitionplanner.draw.service.DrawService
import com.graphite.competitionplanner.draw.service.GroupDrawDTO
import com.graphite.competitionplanner.draw.service.PlayoffDTO
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("competition/{competitionId}/draw/{competitionCategoryId}")
class CompetitionDrawApi(
    val drawService: DrawService,
    val createDraw: CreateDraw,
    val getDraw: GetDraw
) {

    // Can be used both to create initial draw and to make a new draw if desired
    @PutMapping
    fun makeDraw(@PathVariable competitionCategoryId: Int): CompetitionCategoryDrawDTO {
        return createDraw.execute(competitionCategoryId)
    }

    @GetMapping
    fun getDraw(@PathVariable competitionCategoryId: Int): CompetitionCategoryDrawDTO {
        return getDraw.execute(competitionCategoryId)
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

    /*
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
                PlayoffRoundDTO(
                    Round.valueOf(round),
                    matches.filter { it.groupOrRound == round }
                )
            )
        }
        return playoffRoundList
    }*/

}

data class DrawDTO(
    val groupDraw: GroupDrawDTO,
    val playOff: PlayoffDTO
)