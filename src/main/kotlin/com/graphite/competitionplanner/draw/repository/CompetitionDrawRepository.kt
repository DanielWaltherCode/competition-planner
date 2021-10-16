package com.graphite.competitionplanner.draw.repository

import com.graphite.competitionplanner.Tables.MATCH
import com.graphite.competitionplanner.Tables.POOL_DRAW
import com.graphite.competitionplanner.draw.domain.*
import com.graphite.competitionplanner.draw.interfaces.ICompetitionDrawRepository
import com.graphite.competitionplanner.draw.service.MatchType
import com.graphite.competitionplanner.draw.service.PoolDrawHelper
import com.graphite.competitionplanner.tables.records.MatchRecord
import com.graphite.competitionplanner.tables.records.PoolDrawRecord
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

@Repository
class CompetitionDrawRepository(val dslContext: DSLContext) : ICompetitionDrawRepository {

    fun addPoolDraw(poolDrawDto: PoolDrawHelper) {
        val pooldrawRecord = dslContext.newRecord(POOL_DRAW)
        pooldrawRecord.registrationId = poolDrawDto.registrationId
        pooldrawRecord.competitionCategoryId = poolDrawDto.competitionCategoryId
        pooldrawRecord.groupName = poolDrawDto.groupName
        pooldrawRecord.playerNumber = poolDrawDto.playerNumber
        pooldrawRecord.store()
    }

    fun getPoolDraw(competitionCategoryId: Int, groupName: String): List<PoolDrawRecord> {
        return dslContext
            .selectFrom(POOL_DRAW)
            .where(POOL_DRAW.COMPETITION_CATEGORY_ID.eq(competitionCategoryId).and(POOL_DRAW.GROUP_NAME.eq(groupName)))
            .fetchInto(POOL_DRAW)
    }

    fun deleteGroupsInCategory(competitionCategoryId: Int) {
        dslContext.deleteFrom(POOL_DRAW).where(POOL_DRAW.COMPETITION_CATEGORY_ID.eq(competitionCategoryId)).execute()
    }

    fun clearTable() = dslContext.deleteFrom(POOL_DRAW).execute()

    override fun store(draw: CompetitionCategoryDrawSpec) {
        when (draw) {
            is CompetitionCategoryPlayOffDrawSpec -> storePlayoff(draw)
            is CompetitionCategoryGroupsDrawSpec -> storeGroup(draw)
        }
    }

    private fun storePlayoff(draw: CompetitionCategoryPlayOffDrawSpec) {
        val records = draw.matches.map { it.toRecord(draw.competitionCategoryId) }
        dslContext.batchInsert(records).execute()

        // TODO: Create a CompetitionCategoryDrawDTO that is returned
        var matches = dslContext.selectFrom(MATCH).where(MATCH.COMPETITION_CATEGORY_ID.eq(draw.competitionCategoryId))
    }

    private fun PlayOffMatch.toRecord(competitionCategoryId: Int): MatchRecord {
        val record = dslContext.newRecord(MATCH)
        record.startTime = null
        record.endTime = null
        record.competitionCategoryId = competitionCategoryId
        record.matchType = MatchType.PLAYOFF.name
        record.firstRegistrationId = this.registrationOneId.asInt()// matchSpec.firstRegistrationId
        record.secondRegistrationId = this.registrationTwoId.asInt()
        record.matchOrderNumber = this.order
        record.groupOrRound = this.round.name
        return record
    }

    private fun Registration.asInt(): Int {
        return when (this) {
            is Registration.Real -> this.id
            is Registration.Bye -> 0 // TODO: Fetch BYE ID once
            is Registration.Placeholder -> 1 // TODO: Fetch PlaceHolder ID once
        }
    }

    private fun storeGroup(draw: CompetitionCategoryGroupsDrawSpec) {
        // TODO: Implement
    }
}