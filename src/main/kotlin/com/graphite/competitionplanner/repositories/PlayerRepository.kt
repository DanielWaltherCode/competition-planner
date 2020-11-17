package com.graphite.competitionplanner.repositories

import com.graphite.competitionplanner.Tables.CLUB
import com.graphite.competitionplanner.service.PlayerDTO
import com.graphite.competitionplanner.tables.Player.PLAYER
import com.graphite.competitionplanner.tables.records.PlayerRecord
import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.Result
import org.springframework.stereotype.Repository

@Repository
class PlayerRepository(
        val dslContext: DSLContext,
        val clubRepository: ClubRepository
) {

    fun addPlayer(playerDTO: PlayerDTO): PlayerRecord {
        val playerRecord = dslContext.newRecord(PLAYER)
        playerRecord.firstName = playerDTO.firstName
        playerRecord.lastName = playerDTO.lastName
        playerRecord.clubId = playerDTO.club.id
        playerRecord.dateOfBirth = playerDTO.dateOfBirth
        playerRecord.store()

        return playerRecord
    }

    fun deletePlayer(playerId: Int): Boolean {
        val deletedRows = dslContext.deleteFrom(PLAYER).where(PLAYER.ID.eq(playerId)).execute()
        return deletedRows >= 1
    }

    fun getPlayersByClub(clubId: Int): List<Record> {
        return dslContext.select().from(PLAYER).join(CLUB).on(PLAYER.CLUB_ID.eq(CLUB.ID)).fetch()
    }

    fun updatePlayer(playerDTO: PlayerDTO): PlayerRecord {
        val playerRecord = dslContext.newRecord(PLAYER)
        playerRecord.id = playerDTO.id
        playerRecord.firstName = playerDTO.firstName
        playerRecord.lastName = playerDTO.lastName
        playerRecord.clubId = playerDTO.club.id
        playerRecord.dateOfBirth = playerDTO.dateOfBirth
        playerRecord.store()

        return playerRecord
    }

    fun getPlayer(id: Int): Record? {
        return dslContext.select().from(PLAYER).join(CLUB).on(PLAYER.CLUB_ID.eq(CLUB.ID)).where(PLAYER.ID.eq(id)).fetchOne()
    }

    fun findPlayersByPartOfName(partOfName: String): List<Record> {
        return dslContext.select().from(PLAYER).join(CLUB).on(PLAYER.CLUB_ID.eq(CLUB.ID)).where(PLAYER.FIRST_NAME.startsWithIgnoreCase(partOfName)
                .or(PLAYER.LAST_NAME.startsWithIgnoreCase(partOfName))).fetch()
    }

    fun clearTable() {
        dslContext.deleteFrom(PLAYER).execute()
    }

}