package com.graphite.competitionplanner.api

import com.graphite.competitionplanner.repositories.ResultRepository
import com.graphite.competitionplanner.service.ResultDTO
import com.graphite.competitionplanner.service.ResultService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/result")
class ResultApi(val resultService: ResultService, val resultRepository: ResultRepository) {

    @PostMapping("/{matchId}")
    fun addResult(@PathVariable matchId: Int, @RequestBody resultSpec: ResultSpec): ResultDTO {
       return resultService.addResult(matchId, resultSpec)
    }

    @PutMapping("/{matchId}/{gameId}")
    fun updateGameResult(@PathVariable matchId: Int, @PathVariable gameId: Int, @RequestBody gameSpec: GameSpec): ResultDTO {
        return resultService.updateGameResult(matchId, gameId, gameSpec)
    }

    @PutMapping("/{matchId}/partial")
    fun addPartialResult(@PathVariable matchId: Int, @RequestBody resultSpec: ResultSpec): ResultDTO {
        return resultService.addPartialResult(matchId, resultSpec)
    }

    // This is a put request since it's possible that partial results have been added previously
    @PutMapping("/{matchId}")
    fun addFinalMatchResult(@PathVariable matchId: Int, @RequestBody resultSpec: ResultSpec): ResultDTO {
        return resultService.addFinalMatchResult(matchId, resultSpec)
    }

    @GetMapping("/{matchId}")
    fun getMatchResult(@PathVariable matchId: Int): ResultDTO {
        return resultService.getResult(matchId)
    }

    @DeleteMapping("/{matchId}")
    fun removeMatchResult(@PathVariable matchId: Int) {
        resultRepository.deleteMatchResult(matchId)
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