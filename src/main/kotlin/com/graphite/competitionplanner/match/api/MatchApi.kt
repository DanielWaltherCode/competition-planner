package com.graphite.competitionplanner.match.api

import com.graphite.competitionplanner.draw.service.MatchSpec
import com.graphite.competitionplanner.match.service.MatchAndResultDTO
import com.graphite.competitionplanner.match.service.MatchDTO
import com.graphite.competitionplanner.match.service.MatchService
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
@RequestMapping("/match/{competitionId}")
class MatchApi(val matchService: MatchService) {

    @PutMapping("/{matchId}")
    fun updateMatch(@PathVariable matchId: Int, @RequestBody matchSpec: MatchSpec): MatchDTO {
        return matchService.updateMatch(matchId, matchSpec)
    }

    @GetMapping("/{day}")
    fun getMatchesInCompetitionByDay(@PathVariable competitionId: Int, @PathVariable day: LocalDate): List<MatchAndResultDTO> {
        return matchService.getMatchesInCompetitionByDay(competitionId, day)
    }

    @GetMapping()
    fun getAllMatchesInCompetition(@PathVariable competitionId: Int): List<MatchAndResultDTO> {
        return matchService.getMatchesInCompetition(competitionId)
    }

    @GetMapping("single/{matchId}")
    fun getMatch(@PathVariable matchId: Int): MatchAndResultDTO {
        return matchService.getMatchWithResult(matchId)
    }
}