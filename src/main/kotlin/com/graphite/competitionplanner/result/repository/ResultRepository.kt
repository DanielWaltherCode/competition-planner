package com.graphite.competitionplanner.result.repository

import com.graphite.competitionplanner.result.api.GameSpec
import com.graphite.competitionplanner.result.interfaces.IResultRepository
import com.graphite.competitionplanner.result.service.GameDTO
import com.graphite.competitionplanner.tables.Game.GAME
import com.graphite.competitionplanner.tables.records.GameRecord
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

@Repository
class ResultRepository(val dslContext: DSLContext): IResultRepository {
    fun addGameResult(matchId: Int, gameResult: GameSpec): GameRecord {
        val record = dslContext.newRecord(GAME)

        record.matchId = matchId
        record.firstRegistrationResult = gameResult.firstRegistrationResult
        record.secondRegistrationResult = gameResult.secondRegistrationResult
        record.gameNumber = gameResult.gameNumber
        record.store()
        return record
    }

    fun updateGameResult(gameId: Int, matchId: Int, gameResult: GameSpec) {
        dslContext
            .update(GAME)
            .set(GAME.FIRST_REGISTRATION_RESULT, gameResult.firstRegistrationResult)
            .set(GAME.SECOND_REGISTRATION_RESULT, gameResult.secondRegistrationResult)
            .where(GAME.ID.eq(gameId))
            .execute()
    }

    fun getResult(matchId: Int): List<GameRecord> {
        return dslContext.selectFrom(GAME)
            .where(GAME.MATCH_ID.eq(matchId))
            .orderBy(GAME.GAME_NUMBER.asc())
            .fetchInto(GAME)
    }

    fun countResults(): Int? {
        return dslContext.selectCount()
            .from(GAME)
            .fetchOne(0, Int::class.java)
    }

    fun deleteMatchResult(matchId: Int) {
        dslContext
            .deleteFrom(GAME)
            .where(GAME.MATCH_ID.eq(matchId))
            .execute()
    }

    override fun storeResult(matchId: Int, gameResult: GameSpec): GameDTO {
        val record = addGameResult(matchId, gameResult)
        return GameDTO(record.id, record.gameNumber, record.firstRegistrationResult, record.secondRegistrationResult)
    }

    override fun getResults(matchId: Int): List<GameDTO> {
        val records = getResult(matchId)
        return records.map { GameDTO(it.id, it.gameNumber, it.firstRegistrationResult, it.secondRegistrationResult) }
    }

    override fun deleteResults(matchId: Int) {
        deleteMatchResult(matchId)
    }
}