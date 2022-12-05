package com.graphite.competitionplanner.result.repository

import com.graphite.competitionplanner.result.interfaces.IResultRepository
import com.graphite.competitionplanner.result.service.GameDTO
import com.graphite.competitionplanner.result.service.ResultDTO
import com.graphite.competitionplanner.tables.Game.GAME
import com.graphite.competitionplanner.tables.records.GameRecord
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

@Repository
class ResultRepository(val dslContext: DSLContext): IResultRepository {

    override fun getResult(matchId: Int): List<GameDTO> {
        return dslContext.selectFrom(GAME)
            .where(GAME.MATCH_ID.eq(matchId))
            .orderBy(GAME.GAME_NUMBER.asc())
            .fetchInto(GAME)
            .map { it.toDto() }
    }

    override fun getResults(matchIds: List<Int>): MutableMap<Int, ResultDTO> {
        val records = dslContext.selectFrom(GAME).where(GAME.MATCH_ID.`in`(matchIds)).orderBy(GAME.ID.asc()).fetch()
        val matchResultMap = mutableMapOf<Int, ResultDTO>()
        for (id in matchIds) {
            matchResultMap[id] = ResultDTO(records.filter { record -> record.matchId == id }.map { record -> record.toDto() })
        }
        return matchResultMap
    }

    override fun deleteResults(matchId: Int) {
        dslContext
            .deleteFrom(GAME)
            .where(GAME.MATCH_ID.eq(matchId))
            .execute()
    }

    fun GameRecord.toDto(): GameDTO {
        return GameDTO(this.id, this.gameNumber, this.firstRegistrationResult, this.secondRegistrationResult)
    }
}