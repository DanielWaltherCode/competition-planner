package com.graphite.competitionplanner.repositories

import com.graphite.competitionplanner.Tables.*
import com.graphite.competitionplanner.service.competition.MatchSpec
import com.graphite.competitionplanner.service.competition.MatchType
import com.graphite.competitionplanner.tables.records.MatchRecord
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@Repository
class MatchRepository(val dslContext: DSLContext) {

    fun getMatch(matchId: Int): MatchRecord {
        return dslContext.select().from(MATCH).where(MATCH.ID.eq(matchId)).fetchOneInto(MATCH)
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
            .fetchInto(MATCH)
    }

    fun getMatchesInCategoryForMatchType(competitionCategoryId: Int, matchType: MatchType): List<MatchRecord> {
        return dslContext
            .select().from(MATCH)
            .where(MATCH.COMPETITION_CATEGORY_ID.eq(competitionCategoryId)
                .and(MATCH.MATCH_TYPE.equalIgnoreCase(matchType.name)))
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
            .where(MATCH.COMPETITION_CATEGORY_ID.eq(competitionCategoryId).and(MATCH.GROUP_OR_ROUND.equalIgnoreCase(groupName)))
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