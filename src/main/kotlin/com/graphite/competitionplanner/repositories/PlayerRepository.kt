package com.graphite.competitionplanner.repositories

import com.graphite.competitionplanner.Tables.CLUB
import com.graphite.competitionplanner.Tables.PLAYER_RANKING
import com.graphite.competitionplanner.api.PlayerSpec
import com.graphite.competitionplanner.tables.Player.PLAYER
import com.graphite.competitionplanner.tables.records.PlayerRankingRecord
import com.graphite.competitionplanner.tables.records.PlayerRecord
import org.jooq.DSLContext
import org.jooq.Record
import org.springframework.stereotype.Repository

@Repository
class PlayerRepository(
        val dslContext: DSLContext,
        val clubRepository: ClubRepository
) {

    fun addPlayer(playerSpec: PlayerSpec): PlayerRecord {
        val playerRecord = dslContext.newRecord(PLAYER)
        playerRecord.firstName = playerSpec.firstName
        playerRecord.lastName = playerSpec.lastName
        playerRecord.clubId = playerSpec.club.id
        playerRecord.dateOfBirth = playerSpec.dateOfBirth
        playerRecord.store()

        return playerRecord
    }

    fun deletePlayer(playerId: Int): Boolean {
        val deletedRows = dslContext.deleteFrom(PLAYER).where(PLAYER.ID.eq(playerId)).execute()
        return deletedRows >= 1
    }

    fun getPlayersByClub(clubId: Int): List<Record> {
        return dslContext.select().from(PLAYER).join(CLUB).on(PLAYER.CLUB_ID.eq(CLUB.ID)).where(CLUB.ID.eq(clubId)).fetch()
    }

    fun updatePlayer(playerId: Int, playerSpec: PlayerSpec): PlayerRecord {
        val playerRecord = dslContext.newRecord(PLAYER)
        playerRecord.id = playerId
        playerRecord.firstName = playerSpec.firstName
        playerRecord.lastName = playerSpec.lastName
        playerRecord.clubId = playerSpec.club.id
        playerRecord.dateOfBirth = playerSpec.dateOfBirth
        playerRecord.update()

        return playerRecord
    }

    fun getPlayer(id: Int): Record? {
        return dslContext.select().from(PLAYER).join(CLUB).on(PLAYER.CLUB_ID.eq(CLUB.ID)).where(PLAYER.ID.eq(id)).fetchOne()
    }

    fun getPlayerRanking(id: Int): PlayerRankingRecord? {
        return dslContext.select().from(PLAYER_RANKING).where(PLAYER_RANKING.PLAYER_ID.eq(id)).fetchOneInto(PLAYER_RANKING)
    }

    fun findPlayersByPartOfName(partOfName: String): List<Record> {
        return dslContext.select().from(PLAYER).join(CLUB).on(PLAYER.CLUB_ID.eq(CLUB.ID)).where(PLAYER.FIRST_NAME.startsWithIgnoreCase(partOfName)
                .or(PLAYER.LAST_NAME.startsWithIgnoreCase(partOfName))).fetch()
    }

    fun getAll(): List<PlayerRecord> {
        return dslContext.selectFrom(PLAYER).fetchInto(PLAYER)
    }

    fun clearTable() {
        dslContext.deleteFrom(PLAYER).execute()
    }
}