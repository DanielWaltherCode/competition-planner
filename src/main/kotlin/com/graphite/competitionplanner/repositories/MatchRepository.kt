package com.graphite.competitionplanner.repositories

import com.graphite.competitionplanner.Tables.MATCH
import com.graphite.competitionplanner.service.competition.Match
import com.graphite.competitionplanner.service.competition.MatchType
import com.graphite.competitionplanner.tables.records.MatchRecord
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

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

    fun addMatch(match: Match): Int {
        val matchRecord = dslContext.newRecord(MATCH)
        matchRecord.startTime = match.startTime
        matchRecord.endTime = match.endTime
        matchRecord.competitionCategoryId = match.competitionCategoryId
        matchRecord.matchType = match.matchType.name
        matchRecord.firstRegistrationId = match.firstRegistrationId
        matchRecord.secondRegistrationId = match.secondRegistrationId
        matchRecord.matchOrderNumber = match.matchOrderNumber
        matchRecord.groupOrRound = match.groupOrRound
        matchRecord.store()
        return matchRecord.id
    }

    fun updateMatch(matchId: Int, match: Match): MatchRecord {
        val matchRecord = dslContext.newRecord(MATCH)
        matchRecord.id = matchId
        matchRecord.startTime = match.startTime
        matchRecord.endTime = match.endTime
        matchRecord.competitionCategoryId = match.competitionCategoryId
        matchRecord.matchType = match.matchType.name
        matchRecord.firstRegistrationId = match.firstRegistrationId
        matchRecord.secondRegistrationId = match.secondRegistrationId
        matchRecord.matchOrderNumber = match.matchOrderNumber
        matchRecord.groupOrRound = match.groupOrRound
        matchRecord.update()
        return matchRecord
    }

    fun isCategoryDrawn(competitionCategoryId: Int): Boolean {
        return dslContext.fetchExists(
            dslContext.selectFrom(MATCH).where(MATCH.COMPETITION_CATEGORY_ID.eq(competitionCategoryId))
        )
    }

    fun deleteMatchesForCategory(competitionCategoryId: Int) {
        dslContext.deleteFrom(MATCH).where(MATCH.COMPETITION_CATEGORY_ID.eq(competitionCategoryId)
            .and(MATCH.HAS_FINISHED.eq(false))).execute()
    }

    fun clearTable() {
        dslContext.deleteFrom(MATCH).execute()
    }
}