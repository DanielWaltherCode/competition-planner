package com.graphite.competitionplanner.api

import com.graphite.competitionplanner.service.MatchDTO
import com.graphite.competitionplanner.service.MatchService
import com.graphite.competitionplanner.service.competition.MatchSpec
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
@RequestMapping("/match")
class MatchApi(val matchService: MatchService) {

    @PutMapping("/{matchId}")
    fun updateMatch(@PathVariable matchId: Int, @RequestBody matchSpec: MatchSpec): MatchDTO {
        return matchService.updateMatch(matchId, matchSpec)
    }

    @GetMapping("/{competitionId}/{day}")
    fun getMatchesInCompetitionByDay(@PathVariable competitionId: Int, @PathVariable day: LocalDate): List<MatchDTO> {
        return matchService.getMatchesInCompetitionByDay(competitionId, day)
    }

    @GetMapping("/{competitionId}")
    fun getAllMatchesInCompetition(@PathVariable competitionId: Int): List<MatchDTO> {
        return matchService.getMatchesInCompetition(competitionId)
    }
}