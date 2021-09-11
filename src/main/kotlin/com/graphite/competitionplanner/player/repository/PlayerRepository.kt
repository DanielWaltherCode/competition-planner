package com.graphite.competitionplanner.player.repository

import com.graphite.competitionplanner.Tables.CLUB
import com.graphite.competitionplanner.Tables.PLAYER_RANKING
import com.graphite.competitionplanner.club.interfaces.ClubDTO
import com.graphite.competitionplanner.club.repository.ClubRepository
import com.graphite.competitionplanner.common.exception.NotFoundException
import com.graphite.competitionplanner.player.api.PlayerSpec
import com.graphite.competitionplanner.player.domain.interfaces.IPlayerRepository
import com.graphite.competitionplanner.player.domain.interfaces.NewPlayerDTO
import com.graphite.competitionplanner.player.domain.interfaces.PlayerDTO
import com.graphite.competitionplanner.player.domain.interfaces.PlayerWithClubDTO
import com.graphite.competitionplanner.tables.Club
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
) : IPlayerRepository {

    @Deprecated("Use store instead")
    fun addPlayer(playerSpec: PlayerSpec): PlayerRecord {
        val playerRecord = dslContext.newRecord(PLAYER)
        playerRecord.firstName = playerSpec.firstName
        playerRecord.lastName = playerSpec.lastName
        playerRecord.clubId = playerSpec.clubId
        playerRecord.dateOfBirth = playerSpec.dateOfBirth
        playerRecord.store()

        return playerRecord
    }

    @Deprecated("Use playersInClub instead")
    fun getPlayersByClub(clubId: Int): List<Record> {
        return dslContext.select().from(PLAYER).join(CLUB).on(PLAYER.CLUB_ID.eq(CLUB.ID)).where(CLUB.ID.eq(clubId))
            .fetch()
    }

    @Deprecated("Use findById instead")
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

    @Deprecated("Use findByName instead")
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
        playerRecord.clubId = playerSpec.clubId
        playerRecord.dateOfBirth = playerSpec.dateOfBirth
        playerRecord.store()

        return playerRecord
    }

    override fun store(dto: NewPlayerDTO): PlayerDTO {
        val playerRecord = dslContext.newRecord(PLAYER)
        playerRecord.firstName = dto.firstName
        playerRecord.lastName = dto.lastName
        playerRecord.clubId = dto.clubId
        playerRecord.dateOfBirth = dto.dateOfBirth
        playerRecord.store()

        return PlayerDTO(playerRecord.id, dto);
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
            return PlayerDTO(record.id, record.firstName, record.lastName, record.clubId, record.dateOfBirth)
        } else {
            throw NotFoundException("Player with ID $id not found.")
        }
    }

    override fun findByName(startOfName: String): List<PlayerWithClubDTO> {
        val records = dslContext.select().from(PLAYER).join(CLUB).on(PLAYER.CLUB_ID.eq(CLUB.ID)).where(
            PLAYER.FIRST_NAME.startsWithIgnoreCase(startOfName)
                .or(PLAYER.LAST_NAME.startsWithIgnoreCase(startOfName))
        ).fetch()
        return records.map { transformIntoPlayerWithDto(it) }
    }

    private fun transformIntoPlayerWithDto(playerClubRecord: Record): PlayerWithClubDTO {
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
    override fun update(dto: PlayerDTO): PlayerDTO {
        val playerRecord = dslContext.newRecord(PLAYER)
        playerRecord.id = dto.id
        playerRecord.firstName = dto.firstName
        playerRecord.lastName = dto.lastName
        playerRecord.clubId = dto.clubId
        playerRecord.dateOfBirth = dto.dateOfBirth
        val rowsUpdated = playerRecord.update()
        if (rowsUpdated < 1) {
            throw NotFoundException("Could not update. Player with id ${dto.id} not found.")
        }
        return dto
    }

    @Throws(NotFoundException::class)
    override fun delete(dto: PlayerDTO): PlayerDTO {
        val player = getPlayerRecord(dto.id)
        if (player != null) {
            player.delete()
            return PlayerDTO(player.id, player.firstName, player.lastName, player.clubId, player.dateOfBirth)
        } else {
            throw NotFoundException("Could not delete. Player with id ${dto.id} not found.")
        }
    }

    private fun getPlayerRecord(id: Int): PlayerRecord? {
        return dslContext.selectFrom(PLAYER).where(PLAYER.ID.eq(id)).fetchOne()
    }
}