package com.graphite.competitionplanner.match.service

import com.fasterxml.jackson.annotation.JsonFormat
import com.graphite.competitionplanner.competitioncategory.repository.CompetitionCategoryRepository
import com.graphite.competitionplanner.match.domain.MatchType
import com.graphite.competitionplanner.match.repository.MatchRepository
import com.graphite.competitionplanner.player.interfaces.PlayerWithClubDTO
import com.graphite.competitionplanner.registration.domain.Registration
import com.graphite.competitionplanner.registration.domain.asInt
import com.graphite.competitionplanner.registration.service.RegistrationService
import com.graphite.competitionplanner.registration.service.SimpleCompetitionCategoryDTO
import com.graphite.competitionplanner.result.interfaces.IResultRepository
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
    val competitionCategoryRepository: CompetitionCategoryRepository,
    val resultsRepository: IResultRepository
) {

    fun getMatchesInCompetitionByDay(competitionId: Int, day: LocalDate): List<MatchAndResultDTO> {
        val matchRecords = matchRepository.getMatchesInCompetitionByDay(competitionId, day)
        return transformToMatchAndResultDTO(matchRecords)
    }

    fun getMatchesInCategory(competitionCategoryId: Int): List<MatchAndResultDTO> {
        val matchRecords = matchRepository.getMatchesInCategory(competitionCategoryId)
        return matchRecords.map { recordToMatchAndResultDTO(it) }
    }

    fun getGroupMatchesInCategory(competitionCategoryId: Int): List<MatchAndResultDTO> {
        val matchRecords = matchRepository.getMatchesInCategoryForMatchType(competitionCategoryId, MatchType.GROUP)
        return transformToMatchAndResultDTO(matchRecords)
    }

    fun getMatchesInCompetition(competitionId: Int): List<MatchAndResultDTO> {
        val matchRecords = matchRepository.getMatchesInCompetition(competitionId).filter { notContainBye(it) }
        return transformToMatchAndResultDTO(matchRecords)
    }

    private fun notContainBye(record: MatchRecord): Boolean {
        return record.firstRegistrationId != Registration.Bye.asInt() && record.secondRegistrationId != Registration.Bye.asInt()
    }

    fun getMatchesInCompetitionForRegistration(competitionId: Int, registrationId: Int): List<MatchAndResultDTO> {
        val matchRecords = matchRepository.getMatchesInCompetitionForRegistration(competitionId, registrationId)
        return transformToMatchAndResultDTO(matchRecords)
    }

    fun transformToMatchAndResultDTO(matches: List<MatchRecord>): List<MatchAndResultDTO> {
        val results: MutableMap<Int, ResultDTO> = resultsRepository.getResults(matches.map { it.id })
        val resultList = mutableListOf<MatchAndResultDTO>()
        for (match in matches.sortedBy { it.id }) {
            resultList.add(
                MatchAndResultDTO(
                    match.id,
                    match.startTime,
                    match.endTime,
                    SimpleCompetitionCategoryDTO(
                        match.competitionCategoryId,
                        competitionCategoryRepository.getCategoryType(match.competitionCategoryId).categoryName
                    ),
                    match.matchType,
                    registrationService.getPlayersWithClubFromRegistrationId(match.firstRegistrationId),
                    registrationService.getPlayersWithClubFromRegistrationId(match.secondRegistrationId),
                    match.matchOrderNumber,
                    match.groupOrRound,
                    registrationService.getPlayersWithClubFromRegistrationId(match.winner),
                    match.wasWalkover,
                    if (results[match.id] == null) ResultDTO(emptyList()) else results[match.id]!!
                )
            )
        }
        return resultList
    }

    fun recordToMatchAndResultDTO(match: MatchRecord): MatchAndResultDTO {
        val result = resultService.getResult(match.id)

        return MatchAndResultDTO(
            match.id,
            match.startTime,
            match.endTime,
            SimpleCompetitionCategoryDTO(
                match.competitionCategoryId,
                competitionCategoryRepository.getCategoryType(match.competitionCategoryId).categoryName
            ),
            match.matchType,
            registrationService.getPlayersWithClubFromRegistrationId(match.firstRegistrationId),
            registrationService.getPlayersWithClubFromRegistrationId(match.secondRegistrationId),
            match.matchOrderNumber,
            match.groupOrRound,
            registrationService.getPlayersWithClubFromRegistrationId(match.winner),
            match.wasWalkover,
            result
        )
    }

    fun getMatchWithResult(matchId: Int): MatchAndResultDTO {
        val matchRecord = matchRepository.getMatch(matchId)
        return recordToMatchAndResultDTO(matchRecord)
    }

}

data class MatchAndResultDTO(
    val id: Int,
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    val startTime: LocalDateTime?,
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    val endTime: LocalDateTime?,
    val competitionCategory: SimpleCompetitionCategoryDTO,
    val matchType: String,
    val firstPlayer: List<PlayerWithClubDTO>,
    val secondPlayer: List<PlayerWithClubDTO>,
    val matchOrderNumber: Int,
    val groupOrRound: String, // Either group name (e.g. Group "A") or the round like Round of 64, Quarterfinals
    val winner: List<PlayerWithClubDTO>,
    val wasWalkover: Boolean,
    val result: ResultDTO
)