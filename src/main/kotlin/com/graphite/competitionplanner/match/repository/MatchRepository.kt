package com.graphite.competitionplanner.match.repository

import com.graphite.competitionplanner.Tables.*
import com.graphite.competitionplanner.common.exception.NotFoundException
import com.graphite.competitionplanner.draw.interfaces.Round
import com.graphite.competitionplanner.draw.service.MatchSpec
import com.graphite.competitionplanner.draw.service.MatchType
import com.graphite.competitionplanner.match.domain.IMatchRepository
import com.graphite.competitionplanner.match.domain.Match
import com.graphite.competitionplanner.match.domain.PlayoffMatch
import com.graphite.competitionplanner.match.domain.PoolMatch
import com.graphite.competitionplanner.tables.records.MatchRecord
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@Repository
class MatchRepository(val dslContext: DSLContext): IMatchRepository {

    fun getMatch(matchId: Int): MatchRecord {
        return dslContext.select().from(MATCH).where(MATCH.ID.eq(matchId)).fetchOneInto(MATCH)
            ?: throw NotFoundException("Competition category with $matchId not found.")
    }

    override fun getMatch2(matchId: Int): Match {
        val record = dslContext.select().from(MATCH).where(MATCH.ID.eq(matchId)).fetchOneInto(MATCH)
        if (record == null) {
            throw NotFoundException("Competition category with $matchId not found.")
        }else {
            return record.toMatch()
        }
    }

    override fun store(spec: MatchSpec): Match {
        val record = addMatch(spec)
        return record.toMatch()
    }

    fun MatchRecord.toMatch(): Match {
        return if (this.matchType == MatchType.GROUP.name) {
            PoolMatch(
                this.groupOrRound,
                this.id,
                this.competitionCategoryId,
                this.firstRegistrationId,
                this.secondRegistrationId,
                this.wasWalkover,
                this.winner
            )
        } else {
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


    fun getMatchesInCategory(competitionCategoryId: Int): List<MatchRecord> {
        return dslContext
            .select().from(MATCH)
            .where(MATCH.COMPETITION_CATEGORY_ID.eq(competitionCategoryId))
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
            .orderBy(MATCH.ID.asc())
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
            .orderBy(MATCH.ID.asc())
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
            .orderBy(MATCH.ID.asc())
            .fetchInto(MATCH)
    }

    fun getMatchesInCategoryForMatchType(competitionCategoryId: Int, matchType: MatchType): List<MatchRecord> {
        return dslContext
            .select().from(MATCH)
            .where(
                MATCH.COMPETITION_CATEGORY_ID.eq(competitionCategoryId)
                    .and(MATCH.MATCH_TYPE.equalIgnoreCase(matchType.name))
            )
            .fetchInto(MATCH)
    }

    fun getDistinctGroupsInCategory(competitionCategoryId: Int): List<String> {
        return dslContext
            .selectDistinct(MATCH.GROUP_OR_ROUND).from(MATCH)
            .where(MATCH.COMPETITION_CATEGORY_ID.eq(competitionCategoryId))
            .fetchInto(String::class.java)
    }

    fun getDistinctRegistrationIdsInGroup(competitionCategoryId: Int, groupName: String): MutableSet<Int> {
        val records: List<MatchRecord> = dslContext
            .select().from(MATCH)
            .where(
                MATCH.COMPETITION_CATEGORY_ID.eq(competitionCategoryId)
                    .and(MATCH.GROUP_OR_ROUND.equalIgnoreCase(groupName))
            )
            .fetchInto(MATCH)

        val registrationIdSet = mutableSetOf<Int>()
        for (record in records) {
            registrationIdSet.add(record.firstRegistrationId)
            registrationIdSet.add(record.secondRegistrationId)
        }
        return registrationIdSet
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

    fun updateMatch(matchId: Int, matchSpec: MatchSpec): MatchRecord {
        val matchRecord = dslContext.newRecord(MATCH)
        matchRecord.id = matchId
        matchRecord.startTime = matchSpec.startTime
        matchRecord.endTime = matchSpec.endTime
        matchRecord.competitionCategoryId = matchSpec.competitionCategoryId
        matchRecord.matchType = matchSpec.matchType.name
        matchRecord.firstRegistrationId = matchSpec.firstRegistrationId
        matchRecord.secondRegistrationId = matchSpec.secondRegistrationId
        matchRecord.matchOrderNumber = matchSpec.matchOrderNumber
        matchRecord.groupOrRound = matchSpec.groupOrRound
        matchRecord.update()
        return matchRecord
    }

    fun setWinner(matchId: Int, winnerRegistrationId: Int) {
        dslContext.update(MATCH)
            .set(MATCH.WINNER, winnerRegistrationId)
            .where(MATCH.ID.eq(matchId))
            .execute()
    }

    fun isCategoryDrawn(competitionCategoryId: Int): Boolean {
        return dslContext.fetchExists(
            dslContext.selectFrom(MATCH).where(MATCH.COMPETITION_CATEGORY_ID.eq(competitionCategoryId))
        )
    }

    fun deleteMatchesForCategory(competitionCategoryId: Int) {
        dslContext.deleteFrom(MATCH).where(MATCH.COMPETITION_CATEGORY_ID.eq(competitionCategoryId)).execute()
    }

    fun clearTable() {
        dslContext.deleteFrom(MATCH).execute()
    }
}