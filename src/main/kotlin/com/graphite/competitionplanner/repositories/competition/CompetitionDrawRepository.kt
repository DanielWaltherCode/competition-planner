package com.graphite.competitionplanner.repositories.competition

import com.graphite.competitionplanner.Tables.POOL_DRAW
import com.graphite.competitionplanner.service.competition.PoolDrawHelper
import com.graphite.competitionplanner.tables.records.PoolDrawRecord
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

@Repository
class CompetitionDrawRepository(val dslContext: DSLContext) {

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
}