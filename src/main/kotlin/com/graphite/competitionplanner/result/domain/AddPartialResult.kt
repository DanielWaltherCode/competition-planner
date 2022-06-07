package com.graphite.competitionplanner.result.domain

import com.graphite.competitionplanner.match.domain.GameResult
import com.graphite.competitionplanner.match.domain.IMatchRepository
import com.graphite.competitionplanner.result.api.ResultSpec
import com.graphite.competitionplanner.result.service.GameDTO
import com.graphite.competitionplanner.result.service.ResultDTO
import org.springframework.stereotype.Component

@Component
class AddPartialResult(
    val matchRepository: IMatchRepository,
) {
    fun execute(matchId: Int, resultSpec: ResultSpec): ResultDTO {
        val match = matchRepository.getMatch2(matchId)
        match.result = resultSpec.gameList.map { GameResult(0, it.gameNumber, it.firstRegistrationResult, it.secondRegistrationResult) }
        matchRepository.save(match)
        return ResultDTO(match.result.map { GameDTO(it.id, it.number, it.firstRegistrationResult, it.secondRegistrationResult) })
    }
}