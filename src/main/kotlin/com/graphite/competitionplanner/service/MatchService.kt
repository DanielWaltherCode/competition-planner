package com.graphite.competitionplanner.service

import com.graphite.competitionplanner.repositories.MatchRepository
import com.graphite.competitionplanner.repositories.competition.CompetitionCategoryRepository
import com.graphite.competitionplanner.service.competition.MatchSpec
import com.graphite.competitionplanner.service.competition.MatchType
import com.graphite.competitionplanner.tables.records.MatchRecord
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime

@Service
class MatchService(val matchRepository: MatchRepository,
                   val registrationService: RegistrationService,
                   @Lazy val resultService: ResultService,
                   val competitionCategoryRepository: CompetitionCategoryRepository) {

    fun getMatch(matchId: Int): MatchDTO {
        val matchRecord = matchRepository.getMatch(matchId)
        return matchRecordToDTO(matchRecord)
    }

    fun addMatch(matchSpec: MatchSpec): MatchDTO {
        val matchRecord = matchRepository.addMatch(matchSpec)
        return matchRecordToDTO(matchRecord)
    }

    fun deleteMatchesInCategory(competitionCategoryId: Int) {
        matchRepository.deleteMatchesForCategory(competitionCategoryId)
    }

    fun getMatchesInCompetitionByDay(competitionId: Int, day: LocalDate): List<MatchAndResultDTO> {
       val matchRecords = matchRepository.getMatchesInCompetitionByDay(competitionId, day)
        return matchRecords.map { recordToMatchAndResultDTO(it) }
    }

    fun getMatchesInCategory(competitionCategoryId: Int): List<MatchDTO> {
        val matchRecords = matchRepository.getMatchesInCategory(competitionCategoryId)
        return matchRecords.map { matchRecordToDTO(it) }
    }

    fun getGroupMatchesInCategory(competitionCategoryId: Int): List<MatchDTO> {
        val matchRecords = matchRepository.getMatchesInCategoryForMatchType(competitionCategoryId, MatchType.GROUP)
        return matchRecords.map { matchRecordToDTO(it) }
    }

    fun getPlayoffMatchesInCategory(competitionCategoryId: Int): List<MatchDTO> {
        val matchRecords = matchRepository.getMatchesInCategoryForMatchType(competitionCategoryId, MatchType.PLAYOFF)
        return matchRecords.map { matchRecordToDTO(it) }
    }

    fun getMatchesInCompetition(competitionId: Int): List<MatchAndResultDTO> {
        val matchRecords = matchRepository.getMatchesInCompetition(competitionId)
        return matchRecords.map { recordToMatchAndResultDTO(it) }
    }

    fun setWinner(matchId: Int, winnerRegistrationId: Int) {
        matchRepository.setWinner(matchId, winnerRegistrationId)
    }

    fun updateMatch(matchId: Int, matchSpec: MatchSpec): MatchDTO {
        val matchRecord = matchRepository.updateMatch(matchId, matchSpec)
        return matchRecordToDTO(matchRecord)
    }

    fun matchRecordToDTO(record: MatchRecord): MatchDTO {
        return MatchDTO(
            record.id,
            record.startTime,
            record.endTime,
            CompetitionCategoryDTO(record.competitionCategoryId,
                competitionCategoryRepository.getCategoryType(record.competitionCategoryId).categoryName),
            record.matchType,
            registrationService.getPlayersFromRegistrationId(record.firstRegistrationId),
            registrationService.getPlayersFromRegistrationId(record.secondRegistrationId),
            record.matchOrderNumber,
            record.groupOrRound,
            registrationService.getPlayersFromRegistrationId(record.winner)
        )
    }

    fun recordToMatchAndResultDTO(match: MatchRecord): MatchAndResultDTO {
        val result = resultService.getResult(match.id)

        return MatchAndResultDTO(
            match.id,
            match.startTime,
            match.endTime,
            CompetitionCategoryDTO(match.competitionCategoryId,
                competitionCategoryRepository.getCategoryType(match.competitionCategoryId).categoryName),
            match.matchType,
            registrationService.getPlayersFromRegistrationId(match.firstRegistrationId),
            registrationService.getPlayersFromRegistrationId(match.secondRegistrationId),
            match.matchOrderNumber,
            match.groupOrRound,
            registrationService.getPlayersFromRegistrationId(match.winner),
            result
        )
    }

}


data class MatchDTO(
    val id: Int,
    val startTime: LocalDateTime?,
    val endTime: LocalDateTime?,
    val competitionCategory: CompetitionCategoryDTO,
    val matchType: String,
    val firstPlayer: List<PlayerDTO>,
    val secondPlayer: List<PlayerDTO>,
    val matchOrderNumber: Int,
    val groupOrRound: String, // Either group name (e.g. Group "A") or the round like Round of 64, Quarterfinals
    val winner: List<PlayerDTO>
)

data class MatchAndResultDTO(
    val id: Int,
    val startTime: LocalDateTime?,
    val endTime: LocalDateTime?,
    val competitionCategory: CompetitionCategoryDTO,
    val matchType: String,
    val firstPlayer: List<PlayerDTO>,
    val secondPlayer: List<PlayerDTO>,
    val matchOrderNumber: Int,
    val groupOrRound: String, // Either group name (e.g. Group "A") or the round like Round of 64, Quarterfinals
    val winner: List<PlayerDTO>,
    val result: ResultDTO
)