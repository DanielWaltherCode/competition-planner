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

    override fun getResults(matchIds: List<Int>): List<Pair<Int, ResultDTO>> {
        val records = dslContext.selectFrom(GAME).where(GAME.MATCH_ID.`in`(matchIds)).orderBy(GAME.MATCH_ID.asc()).fetch()
        return matchIds.sorted().map {
            Pair(it, ResultDTO(records.filter { record -> record.matchId == it }.map { record -> record.toDto() }))
        }
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