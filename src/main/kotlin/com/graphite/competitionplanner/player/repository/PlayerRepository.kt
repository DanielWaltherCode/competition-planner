package com.graphite.competitionplanner.player.repository

import com.graphite.competitionplanner.Tables.CLUB
import com.graphite.competitionplanner.Tables.PLAYER_RANKING
import com.graphite.competitionplanner.club.interfaces.ClubDTO
import com.graphite.competitionplanner.common.exception.NotFoundException
import com.graphite.competitionplanner.player.interfaces.IPlayerRepository
import com.graphite.competitionplanner.player.interfaces.PlayerDTO
import com.graphite.competitionplanner.player.interfaces.PlayerSpec
import com.graphite.competitionplanner.player.interfaces.PlayerWithClubDTO
import com.graphite.competitionplanner.tables.Club
import com.graphite.competitionplanner.tables.Player.PLAYER
import com.graphite.competitionplanner.tables.records.PlayerRankingRecord
import com.graphite.competitionplanner.tables.records.PlayerRecord
import org.jooq.DSLContext
import org.jooq.Record
import org.springframework.stereotype.Repository

@Repository
class PlayerRepository(val dslContext: DSLContext) : IPlayerRepository {
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
        playerRecord.clubId = playerSpec.clubId
        playerRecord.dateOfBirth = playerSpec.dateOfBirth
        playerRecord.store()

        return playerRecord
    }

    override fun store(spec: PlayerSpec): PlayerDTO {
        val record = dslContext.newRecord(PLAYER)
        record.firstName = spec.firstName
        record.lastName = spec.lastName
        record.clubId = spec.clubId
        record.dateOfBirth = spec.dateOfBirth
        record.store()

        return record.toDto()
    }

    override fun playersInClub(clubId: Int): List<PlayerWithClubDTO> {
        val records =
            dslContext.select().from(PLAYER).join(CLUB).on(PLAYER.CLUB_ID.eq(CLUB.ID)).where(CLUB.ID.eq(clubId))
                .fetch()

        return records.map { record ->
            PlayerWithClubDTO(
                record.into(PLAYER).id,
                record.into(PLAYER).firstName,
                record.into(PLAYER).lastName,
                ClubDTO(record.into(CLUB).id, record.into(CLUB).name, record.into(CLUB).address),
                record.into(PLAYER).dateOfBirth
            )
        }
    }

    @Throws(NotFoundException::class)
    override fun findById(id: Int): PlayerDTO {
        val record = getPlayerRecord(id)
        if (record != null) {
            return record.toDto()
        } else {
            throw NotFoundException("Player with ID $id not found.")
        }
    }

    override fun findAllForIds(playerIds: List<Int>): List<PlayerWithClubDTO> {
        val records =
            dslContext.select().from(PLAYER).join(CLUB).on(PLAYER.CLUB_ID.eq(CLUB.ID)).where(PLAYER.ID.`in`(playerIds))
        return records.map { transformIntoPlayerWithClubDto(it) }
    }

    override fun findByName(startOfName: String): List<PlayerWithClubDTO> {
        val records = dslContext.select().from(PLAYER).join(CLUB).on(PLAYER.CLUB_ID.eq(CLUB.ID)).where(
            PLAYER.FIRST_NAME.startsWithIgnoreCase(startOfName)
                .or(PLAYER.LAST_NAME.startsWithIgnoreCase(startOfName))
        ).fetch()
        return records.map { transformIntoPlayerWithClubDto(it) }
    }

    private fun transformIntoPlayerWithClubDto(playerClubRecord: Record): PlayerWithClubDTO {
        val player = playerClubRecord.into(PLAYER)
        val club = playerClubRecord.into(Club.CLUB)
        return PlayerWithClubDTO(
            player.id,
            player.firstName,
            player.lastName,
            ClubDTO(club.id, club.name, club.address),
            player.dateOfBirth
        )
    }

    @Throws(NotFoundException::class)
    override fun update(id: Int, spec: PlayerSpec): PlayerDTO {
        val record = dslContext.newRecord(PLAYER)
        record.id = id
        record.firstName = spec.firstName
        record.lastName = spec.lastName
        record.clubId = spec.clubId
        record.dateOfBirth = spec.dateOfBirth
        val rowsUpdated = record.update()
        if (rowsUpdated < 1) {
            throw NotFoundException("Could not update. Player with id $id not found.")
        }
        return record.toDto()
    }

    @Throws(NotFoundException::class)
    override fun delete(id: Int): PlayerDTO {
        val player = getPlayerRecord(id)
        if (player != null) {
            player.delete()
            return PlayerDTO(player.id, player.firstName, player.lastName, player.clubId, player.dateOfBirth)
        } else {
            throw NotFoundException("Could not delete. Player with id $id not found.")
        }
    }

    fun PlayerRecord.toDto(): PlayerDTO {
        return PlayerDTO(this.id, this.firstName, this.lastName, this.clubId, this.dateOfBirth)
    }

    private fun getPlayerRecord(id: Int): PlayerRecord? {
        return dslContext.selectFrom(PLAYER).where(PLAYER.ID.eq(id)).fetchOne()
    }
}