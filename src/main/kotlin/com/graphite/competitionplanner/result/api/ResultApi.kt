package com.graphite.competitionplanner.result.api

import com.graphite.competitionplanner.result.domain.AddPartialResult
import com.graphite.competitionplanner.result.service.ResultDTO
import com.graphite.competitionplanner.result.service.ResultService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("api/result/{competitionId}")
class ResultApi(
    val resultService: ResultService,
    val addPartialResult: AddPartialResult
) {

    @PutMapping("/{matchId}/partial")
    fun addPartialResult(@PathVariable matchId: Int, @RequestBody resultSpec: ResultSpec): ResultDTO {
        return addPartialResult.execute(matchId, resultSpec)
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
    fun deleteMatchResult(@PathVariable matchId: Int) {
        resultService.deleteResults(matchId)
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