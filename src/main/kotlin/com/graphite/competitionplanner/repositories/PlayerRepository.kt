package com.graphite.competitionplanner.repositories

import com.graphite.competitionplanner.api.PlayerDTO
import com.graphite.competitionplanner.tables.Player.PLAYER
import com.graphite.competitionplanner.tables.pojos.Player
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

@Repository
class PlayerRepository(
        val dslContext: DSLContext
) {

    fun addPlayer(playerDTO: PlayerDTO): Player {
        val playerRecord = dslContext.newRecord(PLAYER)
        playerRecord.firstName = playerDTO.firstName
        playerRecord.lastName = playerDTO.lastName
        playerRecord.clubId = playerDTO.clubId
        playerRecord.dateOfBirth = playerDTO.dateOfBirth
        playerRecord.store()

        return Player(playerRecord.id, playerDTO.firstName, playerDTO.lastName, playerDTO.clubId, playerDTO.dateOfBirth)
    }

    fun deletePlayer(playerId: Int): Boolean {
        val deletedRows = dslContext.deleteFrom(PLAYER).where(PLAYER.ID.eq(playerId)).execute()
        return deletedRows >= 1
    }

    fun getPlayers(): List<Player> {
        return dslContext.selectFrom(PLAYER).fetchInto(Player::class.java)
    }

    fun updatePlayer(playerDTO: PlayerDTO): Player {
        val playerRecord = dslContext.newRecord(PLAYER)
        playerRecord.id = playerDTO.id
        playerRecord.firstName = playerDTO.firstName
        playerRecord.lastName = playerDTO.lastName
        playerRecord.clubId = playerDTO.clubId
        playerRecord.dateOfBirth = playerDTO.dateOfBirth
        playerRecord.store()

        return Player(playerRecord.id, playerDTO.firstName, playerDTO.lastName, playerDTO.clubId, playerDTO.dateOfBirth)
    }

    fun getPlayer(id: Int): Player? {
        return dslContext.select().from(PLAYER).where(PLAYER.ID.eq(id)).fetchOneInto(Player::class.java)
    }

    fun findPlayersByPartOfName(partOfName: String): List<Player>? {
        return dslContext.select().from(PLAYER).where(PLAYER.FIRST_NAME.startsWithIgnoreCase(partOfName)
                .or(PLAYER.LAST_NAME.startsWithIgnoreCase(partOfName))).fetchInto(Player::class.java)
    }

    fun clearTable() {
        dslContext.deleteFrom(PLAYER).execute()
    }
}