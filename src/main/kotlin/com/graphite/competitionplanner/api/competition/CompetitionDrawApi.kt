package com.graphite.competitionplanner.api.competition

import com.graphite.competitionplanner.service.competition.CompetitionDrawService
import com.graphite.competitionplanner.service.competition.GroupDrawDTO
import com.graphite.competitionplanner.service.competition.PlayoffDTO
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/competition/{competitionId}/draw/{competitionCategoryId}")
class CompetitionDrawApi(val drawService: CompetitionDrawService) {

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


}

data class DrawDTO(
    val groupDraw: GroupDrawDTO,
    val playOff: PlayoffDTO
)