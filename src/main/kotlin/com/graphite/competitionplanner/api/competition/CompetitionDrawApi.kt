package com.graphite.competitionplanner.api.competition

import com.graphite.competitionplanner.service.MatchDTO
import com.graphite.competitionplanner.service.PlayerDTO
import com.graphite.competitionplanner.service.competition.CompetitionDrawService
import com.graphite.competitionplanner.tables.records.MatchRecord
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/competition/{competitionId}/draw/{competitionCategoryId}")
class CompetitionDrawApi(val drawService: CompetitionDrawService) {

    @GetMapping
    fun makeDraw(@PathVariable competitionCategoryId: Int): List<MatchDTO> {
        return drawService.createDraw(competitionCategoryId)
    }
}