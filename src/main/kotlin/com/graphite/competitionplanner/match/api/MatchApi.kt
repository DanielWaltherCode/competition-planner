package com.graphite.competitionplanner.match.api

import com.graphite.competitionplanner.match.service.MatchAndResultDTO
import com.graphite.competitionplanner.match.service.MatchService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@RestController
@RequestMapping("/match/{competitionId}")
class MatchApi(val matchService: MatchService) {

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