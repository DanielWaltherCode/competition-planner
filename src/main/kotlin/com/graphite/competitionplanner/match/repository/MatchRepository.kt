package com.graphite.competitionplanner.match.repository

import com.graphite.competitionplanner.Tables.*
import com.graphite.competitionplanner.common.exception.NotFoundException
import com.graphite.competitionplanner.common.repository.BaseRepository
import com.graphite.competitionplanner.draw.interfaces.Round
import com.graphite.competitionplanner.draw.service.MatchSpec
import com.graphite.competitionplanner.match.domain.*
import com.graphite.competitionplanner.tables.records.GameRecord
import com.graphite.competitionplanner.tables.records.MatchRecord
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@Repository
class MatchRepository(
    dslContext: DSLContext
) : BaseRepository(dslContext),
    IMatchRepository
{

    fun getMatch(matchId: Int): MatchRecord {
        return dslContext.select().from(MATCH).where(MATCH.ID.eq(matchId)).fetchOneInto(MATCH)
                ?: throw NotFoundException("Competition category with $matchId not found.")
    }

    override fun getMatch2(matchId: Int): Match {
        val record = dslContext.select().from(MATCH).where(MATCH.ID.eq(matchId)).fetchOneInto(MATCH)
        val gameRecords = dslContext.selectFrom(GAME).where(GAME.MATCH_ID.eq(matchId)).fetch()

        if (record == null) {
            throw NotFoundException("Competition category with $matchId not found.")
        } else {
            val match = record.toMatch()
            match.result = gameRecords.map { it.toGameResult() }
            return match
        }
    }

    override fun store(spec: MatchSpec): Match {
        val record = addMatch(spec)
        return record.toMatch()
    }

    override fun save(match: Match) {
        val matchRecord = match.toRecord()
        val gameRecords = match.result.map { it.toRecord(match.id) }

        asTransaction {
            dslContext.deleteFrom(GAME).where(GAME.MATCH_ID.eq(match.id)).execute()
            matchRecord.update()
            dslContext.batchStore(gameRecords).execute()
        }
    }

    override fun updateMatchTime(matchId: Int, matchTime: LocalDateTime) {
        dslContext
                .update(MATCH)
                .set(MATCH.START_TIME, matchTime)
                .where(MATCH.ID.eq(matchId))
                .execute()
    }

    override fun removeMatchTimeForCategory(categoryId: Int, stage: MatchType) {
        dslContext
                .update(MATCH)
                .setNull(MATCH.START_TIME)
                .where(MATCH.COMPETITION_CATEGORY_ID.eq(categoryId).and(MATCH.MATCH_TYPE.eq(stage.name)))
                .execute()
    }

    fun Match.toRecord(): MatchRecord {
        val record = dslContext.newRecord(MATCH)
        record.id = this.id
        record.competitionCategoryId = this.competitionCategoryId
        record.firstRegistrationId = this.firstRegistrationId
        record.secondRegistrationId = this.secondRegistrationId
        record.wasWalkover = this.wasWalkOver
        record.winner = this.winner
        record.startTime = this.startTime
        record.endTime = this.endTime

        when (this) {
            is PoolMatch -> {
                record.groupOrRound = name
            }
            is PlayoffMatch -> {
                record.groupOrRound = round.name
                record.matchOrderNumber = orderNumber
            }
        }

        // In JOOQ, you have to reset fields that you do not want to update. Not doing this would set fields to null
        record.reset(MATCH.MATCH_TYPE)
        record.reset(MATCH.START_TIME)
        record.reset(MATCH.END_TIME)

        return record
    }

    private fun GameResult.toRecord(matchId: Int): GameRecord {
        val record = dslContext.newRecord(GAME)
        record.matchId = matchId
        record.gameNumber = this.number
        record.firstRegistrationResult = this.firstRegistrationResult
        record.secondRegistrationResult = this.secondRegistrationResult
        return record
    }

    private fun MatchRecord.toMatch(): Match {
        return when (MatchType.valueOf(this.matchType)) {
            MatchType.GROUP -> {
                PoolMatch(
                    this.groupOrRound,
                    this.id,
                    this.competitionCategoryId,
                    this.firstRegistrationId,
                    this.secondRegistrationId,
                    this.wasWalkover,
                    this.winner
                )
            }
            MatchType.PLAYOFF, MatchType.B_PLAYOFF -> {
                PlayoffMatch(
                    Round.valueOf(this.groupOrRound),
                    this.matchOrderNumber,
                    this.id,
                    this.competitionCategoryId,
                    this.firstRegistrationId,
                    this.secondRegistrationId,
                    this.wasWalkover,
                    this.winner
                )
            }
        }
    }

    private fun GameRecord.toGameResult(): GameResult {
        return GameResult(
                this.id,
                this.gameNumber,
                this.firstRegistrationResult,
                this.secondRegistrationResult
        )
    }

    fun getMatchesInCategory(competitionCategoryId: Int): List<MatchRecord> {
        return dslContext
                .select().from(MATCH)
                .where(MATCH.COMPETITION_CATEGORY_ID.eq(competitionCategoryId))
                .orderBy(MATCH.START_TIME.asc().nullsLast())
                .fetchInto(MATCH)
    }

    fun getMatchesInCompetitionByDay(competitionId: Int, day: LocalDate): List<MatchRecord> {
        val startTime = LocalDateTime.of(day, LocalTime.of(0, 0))
        val endTime = LocalDateTime.of(day, LocalTime.of(23, 59))
        return dslContext
                .select().from(COMPETITION)
                .join(COMPETITION_CATEGORY)
                .on(COMPETITION.ID.eq(COMPETITION_CATEGORY.COMPETITION_ID))
                .join(MATCH)
                .on(COMPETITION_CATEGORY.ID.eq(MATCH.COMPETITION_CATEGORY_ID))
                .where(COMPETITION.ID.eq(competitionId)).and(MATCH.START_TIME.between(startTime, endTime))
                .orderBy(MATCH.START_TIME.asc().nullsLast())
                .fetchInto(MATCH)
    }

    fun getMatchesInCompetition(competitionId: Int): List<MatchRecord> {
        return dslContext
                .select().from(COMPETITION)
                .join(COMPETITION_CATEGORY)
                .on(COMPETITION.ID.eq(COMPETITION_CATEGORY.COMPETITION_ID))
                .join(MATCH)
                .on(COMPETITION_CATEGORY.ID.eq(MATCH.COMPETITION_CATEGORY_ID))
                .where(COMPETITION.ID.eq(competitionId))
                .orderBy(MATCH.START_TIME.asc().nullsLast(), MATCH.COMPETITION_CATEGORY_ID, MATCH.GROUP_OR_ROUND)
                .fetchInto(MATCH)
    }

    fun getMatchesInCompetitionForRegistration(competitionId: Int, registrationId: Int): List<MatchRecord> {
        return dslContext
                .select().from(COMPETITION)
                .join(COMPETITION_CATEGORY)
                .on(COMPETITION.ID.eq(COMPETITION_CATEGORY.COMPETITION_ID))
                .join(MATCH)
                .on(COMPETITION_CATEGORY.ID.eq(MATCH.COMPETITION_CATEGORY_ID))
                .where(COMPETITION.ID.eq(competitionId))
                .and(
                        MATCH.FIRST_REGISTRATION_ID.eq(registrationId)
                                .or(MATCH.SECOND_REGISTRATION_ID.eq(registrationId))
                )
                .orderBy(MATCH.START_TIME.asc().nullsLast())
                .fetchInto(MATCH)
    }

    fun getMatchesInCategoryForMatchType(competitionCategoryId: Int, matchType: MatchType): List<MatchRecord> {
        return dslContext
                .select().from(MATCH)
                .where(
                        MATCH.COMPETITION_CATEGORY_ID.eq(competitionCategoryId)
                                .and(MATCH.MATCH_TYPE.equalIgnoreCase(matchType.name))
                )
                .orderBy(MATCH.START_TIME.asc().nullsLast())
                .fetchInto(MATCH)
    }

    fun addMatch(matchSpec: MatchSpec): MatchRecord {
        val matchRecord = dslContext.newRecord(MATCH)
        matchRecord.startTime = matchSpec.startTime
        matchRecord.endTime = matchSpec.endTime
        matchRecord.competitionCategoryId = matchSpec.competitionCategoryId
        matchRecord.matchType = matchSpec.matchType.name
        matchRecord.firstRegistrationId = matchSpec.firstRegistrationId
        matchRecord.secondRegistrationId = matchSpec.secondRegistrationId
        matchRecord.matchOrderNumber = matchSpec.matchOrderNumber
        matchRecord.groupOrRound = matchSpec.groupOrRound
        matchRecord.wasWalkover = false // Default value in database
        matchRecord.store()
        return matchRecord
    }

    fun clearTable() {
        dslContext.deleteFrom(MATCH).execute()
    }
}