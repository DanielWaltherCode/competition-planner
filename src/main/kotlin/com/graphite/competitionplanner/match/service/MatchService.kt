package com.graphite.competitionplanner.match.service

import com.graphite.competitionplanner.competitioncategory.repository.CompetitionCategoryRepository
import com.graphite.competitionplanner.draw.service.MatchSpec
import com.graphite.competitionplanner.draw.service.MatchType
import com.graphite.competitionplanner.match.repository.MatchRepository
import com.graphite.competitionplanner.player.interfaces.PlayerDTO
import com.graphite.competitionplanner.player.interfaces.PlayerWithClubDTO
import com.graphite.competitionplanner.registration.service.SimpleCompetitionCategoryDTO
import com.graphite.competitionplanner.registration.service.RegistrationService
import com.graphite.competitionplanner.result.service.ResultDTO
import com.graphite.competitionplanner.result.service.ResultService
import com.graphite.competitionplanner.tables.records.MatchRecord
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime

@Service
class MatchService(
    val matchRepository: MatchRepository,
    @Lazy val registrationService: RegistrationService,
    @Lazy val resultService: ResultService,
    val competitionCategoryRepository: CompetitionCategoryRepository
) {

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

    fun getMatchesInCategory(competitionCategoryId: Int): List<MatchAndResultDTO> {
        val matchRecords = matchRepository.getMatchesInCategory(competitionCategoryId)
        return matchRecords.map { recordToMatchAndResultDTO(it) }
    }

    fun getGroupMatchesInCategory(competitionCategoryId: Int): List<MatchAndResultDTO> {
        val matchRecords = matchRepository.getMatchesInCategoryForMatchType(competitionCategoryId, MatchType.GROUP)
        return matchRecords.map { recordToMatchAndResultDTO(it) }
    }

    fun getPlayoffMatchesInCategory(competitionCategoryId: Int): List<MatchDTO> {
        val matchRecords = matchRepository.getMatchesInCategoryForMatchType(competitionCategoryId, MatchType.PLAYOFF)
        return matchRecords.map { matchRecordToDTO(it) }
    }

    fun getMatchesInCompetition(competitionId: Int): List<MatchAndResultDTO> {
        val matchRecords = matchRepository.getMatchesInCompetition(competitionId)
        return matchRecords.map { recordToMatchAndResultDTO(it) }
    }

    fun getMatchesInCompetitionForRegistration(competitionId: Int, registrationId: Int): List<MatchAndResultDTO> {
        val matchRecords = matchRepository.getMatchesInCompetitionForRegistration(competitionId, registrationId)
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
            SimpleCompetitionCategoryDTO(record.competitionCategoryId,
                competitionCategoryRepository.getCategoryType(record.competitionCategoryId).categoryName),
            record.matchType,
            registrationService.getPlayersWithClubFromRegistrationId(record.firstRegistrationId),
            registrationService.getPlayersWithClubFromRegistrationId(record.secondRegistrationId),
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
            SimpleCompetitionCategoryDTO(match.competitionCategoryId,
                competitionCategoryRepository.getCategoryType(match.competitionCategoryId).categoryName),
            match.matchType,
            registrationService.getPlayersWithClubFromRegistrationId(match.firstRegistrationId),
            registrationService.getPlayersWithClubFromRegistrationId(match.secondRegistrationId),
            match.matchOrderNumber,
            match.groupOrRound,
            registrationService.getPlayersWithClubFromRegistrationId(match.winner),
            result
        )
    }

    fun getMatchWithResult(matchId: Int): MatchAndResultDTO {
        val matchRecord = matchRepository.getMatch(matchId)
        return recordToMatchAndResultDTO(matchRecord)
    }

}


data class MatchDTO(
    val id: Int,
    val startTime: LocalDateTime?,
    val endTime: LocalDateTime?,
    val competitionCategory: SimpleCompetitionCategoryDTO,
    val matchType: String,
    val firstPlayer: List<PlayerWithClubDTO>,
    val secondPlayer: List<PlayerWithClubDTO>,
    val matchOrderNumber: Int,
    val groupOrRound: String, // Either group name (e.g. Group "A") or the round like Round of 64, Quarterfinals
    val winner: List<PlayerDTO>
)

data class MatchAndResultDTO(
    val id: Int,
    val startTime: LocalDateTime?,
    val endTime: LocalDateTime?,
    val competitionCategory: SimpleCompetitionCategoryDTO,
    val matchType: String,
    val firstPlayer: List<PlayerWithClubDTO>,
    val secondPlayer: List<PlayerWithClubDTO>,
    val matchOrderNumber: Int,
    val groupOrRound: String, // Either group name (e.g. Group "A") or the round like Round of 64, Quarterfinals
    val winner: List<PlayerWithClubDTO>,
    val result: ResultDTO
)