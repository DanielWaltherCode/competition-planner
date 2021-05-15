package com.graphite.competitionplanner.repositories

import com.graphite.competitionplanner.Tables.CLUB
import com.graphite.competitionplanner.Tables.PLAYER_RANKING
import com.graphite.competitionplanner.api.PlayerSpec
import com.graphite.competitionplanner.domain.dto.ClubDTO
import com.graphite.competitionplanner.domain.dto.PlayerDTO
import com.graphite.competitionplanner.domain.interfaces.IPlayerRepository
import com.graphite.competitionplanner.tables.Player.PLAYER
import com.graphite.competitionplanner.tables.records.PlayerRankingRecord
import com.graphite.competitionplanner.tables.records.PlayerRecord
import org.jooq.DSLContext
import org.jooq.Record
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Repository

@Repository
class PlayerRepository(
    val dslContext: DSLContext,
    val clubRepository: ClubRepository
) : IPlayerRepository {
    private val logger = LoggerFactory.getLogger(javaClass)


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
        return dslContext.select().from(PLAYER).join(CLUB).on(PLAYER.CLUB_ID.eq(CLUB.ID)).where(CLUB.ID.eq(clubId))
            .fetch()
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
        return dslContext.select().from(PLAYER).join(CLUB).on(PLAYER.CLUB_ID.eq(CLUB.ID)).where(PLAYER.ID.eq(id))
            .fetchOne()
    }

    fun getPlayerRanking(playerId: Int): PlayerRankingRecord? {
        return dslContext.select().from(PLAYER_RANKING).where(PLAYER_RANKING.PLAYER_ID.eq(playerId))
            .fetchOneInto(PLAYER_RANKING)
    }

    fun addPlayerRanking(playerId: Int, rankToAdd: Int, categoryType: String) {
        val currentRecord = getPlayerRanking(playerId)

        if (currentRecord != null) {
            if (categoryType.toUpperCase() == "SINGLES") {
                currentRecord.rankSingle += rankToAdd
                currentRecord.update()
            } else if (categoryType.toUpperCase() == "DOUBLES") {
                currentRecord.rankDouble += rankToAdd
                currentRecord.update()
            }
        } else {
            val record = dslContext.newRecord(PLAYER_RANKING)
            record.playerId = playerId
            if (categoryType.toUpperCase() == "SINGLES") {
                record.rankSingle = rankToAdd
                record.store()
            } else if (categoryType.toUpperCase() == "DOUBLES") {
                record.rankDouble = rankToAdd
                record.store()
            }
        }
    }

    fun findPlayersByPartOfName(partOfName: String): List<Record> {
        return dslContext.select().from(PLAYER).join(CLUB).on(PLAYER.CLUB_ID.eq(CLUB.ID)).where(
            PLAYER.FIRST_NAME.startsWithIgnoreCase(partOfName)
                .or(PLAYER.LAST_NAME.startsWithIgnoreCase(partOfName))
        ).fetch()
    }

    fun getAll(): List<PlayerRecord> {
        return dslContext.selectFrom(PLAYER).fetchInto(PLAYER)
    }

    fun clearTable() {
        dslContext.deleteFrom(PLAYER).execute()
    }

    fun clearRankingTable() {
        dslContext.deleteFrom(PLAYER_RANKING).execute()
    }

    fun addPlayerWithId(id: Int, playerSpec: PlayerSpec): PlayerRecord {
        val playerRecord = dslContext.newRecord(PLAYER)
        playerRecord.id = id
        playerRecord.firstName = playerSpec.firstName
        playerRecord.lastName = playerSpec.lastName
        playerRecord.clubId = playerSpec.club.id
        playerRecord.dateOfBirth = playerSpec.dateOfBirth
        playerRecord.store()

        return playerRecord
    }

    override fun store(dto: PlayerDTO): PlayerDTO {
        val playerRecord = dslContext.newRecord(PLAYER)
        playerRecord.firstName = dto.firstName
        playerRecord.lastName = dto.lastName
        playerRecord.clubId = dto.club.id
        playerRecord.dateOfBirth = dto.dateOfBirth
        playerRecord.store()

        return PlayerDTO(playerRecord.id, dto);
    }

    override fun playersInClub(dto: ClubDTO): List<PlayerDTO> {
        val records =
            dslContext.select().from(PLAYER).join(CLUB).on(PLAYER.CLUB_ID.eq(CLUB.ID)).where(CLUB.ID.eq(dto.id))
                .fetchInto(PLAYER)

        return records.map { record ->
            PlayerDTO(
                record.id,
                record.firstName,
                record.lastName,
                ClubDTO(record.clubId, "", ""),
                record.dateOfBirth
            )
        }
    }
}