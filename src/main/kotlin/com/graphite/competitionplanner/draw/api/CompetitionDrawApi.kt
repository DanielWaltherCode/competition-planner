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

}

data class DrawDTO(
    val groupDraw: GroupDrawDTO,
    val playOff: PlayoffDTO
)