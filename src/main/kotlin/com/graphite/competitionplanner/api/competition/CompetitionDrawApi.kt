package com.graphite.competitionplanner.api.competition

import com.graphite.competitionplanner.service.draw.DrawService
import com.graphite.competitionplanner.service.draw.GroupDrawDTO
import com.graphite.competitionplanner.service.draw.PlayoffDTO
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("competition/{competitionId}/draw/{competitionCategoryId}")
class CompetitionDrawApi(val drawService: DrawService) {

    // Can be used both create initial draw and to make a new draw if desired
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

}

data class DrawDTO(
    val groupDraw: GroupDrawDTO,
    val playOff: PlayoffDTO
)