package com.graphite.competitionplanner.api

import com.graphite.competitionplanner.service.ResultDTO
import com.graphite.competitionplanner.service.ResultService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/result")
class ResultApi(val resultService: ResultService) {

    // Get per day in competition

    @PostMapping("/{matchId}")
    fun addResult(@PathVariable matchId: Int, @RequestBody resultSpec: ResultSpec): ResultDTO {
       return resultService.addResult(matchId, resultSpec)
    }

    @PutMapping("/{matchId}/{gameId}")
    fun updateGameResult(@PathVariable matchId: Int, @PathVariable gameId: Int, @RequestBody gameSpec: GameSpec): ResultDTO {
        return resultService.updateGameResult(matchId, gameId, gameSpec)
    }

    @PutMapping("/{matchId}")
    fun updateFullMatchResult(@PathVariable matchId: Int, @RequestBody resultSpec: ResultSpec): ResultDTO {
        return resultService.updateFullMatchResult(matchId, resultSpec)
    }

    @GetMapping("/{matchId}")
    fun getMatchResult(@PathVariable matchId: Int): ResultDTO {
        return resultService.getResult(matchId)
    }
}

data class ResultSpec(
    val gameList: List<GameSpec>
)

data class GameSpec(
    val gameNumber: Int,
    val firstRegistrationResult: Int,
    val secondRegistrationResult: Int
)