package com.graphite.competitionplanner.service

import com.graphite.competitionplanner.repositories.MatchRepository
import com.graphite.competitionplanner.service.competition.Match
import com.graphite.competitionplanner.tables.records.MatchRecord
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class MatchService(val matchRepository: MatchRepository,
val registrationService: RegistrationService) {

    fun getMatch(matchId: Int): MatchDTO {
        val matchRecord = matchRepository.getMatch(matchId)
        return matchRecordToDTO(matchRecord)
    }

    fun deleteMatchesInCategory(competitionCategoryId: Int) {
        matchRepository.deleteMatchesForCategory(competitionCategoryId)
    }

    fun getMatchesInCategory(competitionCategoryId: Int): List<MatchDTO> {
        val matchRecords = matchRepository.getMatchesInCategory(competitionCategoryId)
        return matchRecords.map { matchRecordToDTO(it) }
    }

    fun updateMatch(matchId: Int, match: Match): MatchDTO {
        val matchRecord = matchRepository.updateMatch(matchId, match)
        return matchRecordToDTO(matchRecord)
    }

    fun matchRecordToDTO(record: MatchRecord): MatchDTO {
        return MatchDTO(
            record.id,
            record.startTime,
            record.endTime,
            record.competitionCategoryId,
            record.matchType,
            registrationService.getPlayersFromRegistrationId(record.firstRegistrationId),
            registrationService.getPlayersFromRegistrationId(record.secondRegistrationId),
            record.matchOrderNumber,
            record.groupOrRound
        )
    }
}


data class MatchDTO(
    val id: Int,
    val startTime: LocalDateTime?,
    val endTime: LocalDateTime?,
    val competitionCategoryId: Int,
    val matchType: String,
    val firstPlayer: List<PlayerDTO>,
    val secondPlayer: List<PlayerDTO>,
    val matchOrderNumber: Int,
    val groupOrRound: String // Either group name (e.g. Group "A") or the round like Round of 64, Quarterfinals
)