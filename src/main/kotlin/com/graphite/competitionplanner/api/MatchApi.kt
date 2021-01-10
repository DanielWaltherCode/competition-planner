package com.graphite.competitionplanner.api

import com.graphite.competitionplanner.service.MatchDTO
import com.graphite.competitionplanner.service.MatchService
import com.graphite.competitionplanner.service.competition.Match
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/match")
class MatchApi(val matchService: MatchService) {

    @PutMapping("/{matchId}")
    fun updateMatch(@PathVariable matchId: Int, @RequestBody match: Match): MatchDTO {
        return matchService.updateMatch(matchId, match)
    }
}