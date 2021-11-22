package com.graphite.competitionplanner.club.repository

import com.graphite.competitionplanner.Tables.*
import com.graphite.competitionplanner.club.interfaces.ClubDTO
import com.graphite.competitionplanner.club.interfaces.ClubSpec
import com.graphite.competitionplanner.club.interfaces.IClubRepository
import com.graphite.competitionplanner.common.exception.NotFoundException
import com.graphite.competitionplanner.tables.Club.CLUB
import com.graphite.competitionplanner.tables.records.ClubRecord
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

@Repository
class ClubRepository(val dslContext: DSLContext) : IClubRepository {

    override fun getAll(): List<ClubDTO> {
        val records = dslContext.select().from(CLUB).fetchInto(CLUB)
        return records.map { ClubDTO(it.id, it.name, it.address) }
    }

    override fun store(spec: ClubSpec): ClubDTO {
        val record: ClubRecord = dslContext.newRecord(CLUB)
        record.name = spec.name
        record.address = spec.address
        record.store()
        return record.toDto()
    }

    @Throws(NotFoundException::class)
    override fun findByName(name: String): ClubDTO {
        val record = dslContext.selectFrom(CLUB).where(CLUB.NAME.eq(name)).fetchOne()
        if (record != null) {
            return ClubDTO(record.id, record.name, record.address)
        } else {
            throw NotFoundException("Club with name $name not found.")
        }
    }

    @Throws(NotFoundException::class)
    override fun findById(id: Int): ClubDTO {
        val record = dslContext.selectFrom(CLUB).where(CLUB.ID.eq(id)).fetchOne()
        if (record != null) {
            return ClubDTO(record.id, record.name, record.address)
        } else {
            throw NotFoundException("Club with ID $id not found.")
        }
    }

    @Throws(NotFoundException::class)
    override fun delete(clubId: Int): Boolean {
        val deletedRows = dslContext.deleteFrom(CLUB).where(CLUB.ID.eq(clubId)).execute()
        if (deletedRows >= 1) {
            return true
        } else {
            throw NotFoundException("Could not delete. Club with id $clubId not found.")
        }
    }

    @Throws(NotFoundException::class)
    override fun update(clubId: Int, spec: ClubSpec): ClubDTO {
        val record: ClubRecord = dslContext.newRecord(CLUB)
        record.id = clubId
        record.name = spec.name
        record.address = spec.address
        val rowsUpdated = record.update()
        if (rowsUpdated < 1) {
            throw NotFoundException("Could not update. Club with id $clubId not found.")
        }
        return record.toDto()
    }

    override fun getClubsInCompetition(competitionId: Int): List<ClubDTO> {
       val clubRecords = dslContext.selectDistinct(CLUB.asterisk())
           .from(CLUB)
           .join(PLAYER).on(PLAYER.CLUB_ID.eq(CLUB.ID))
           .join(PLAYER_REGISTRATION).on(PLAYER_REGISTRATION.PLAYER_ID.eq(PLAYER.ID))
           .join(COMPETITION_CATEGORY_REGISTRATION).on(COMPETITION_CATEGORY_REGISTRATION.REGISTRATION_ID.eq(
               PLAYER_REGISTRATION.REGISTRATION_ID))
           .join(COMPETITION_CATEGORY).on(COMPETITION_CATEGORY.ID.eq(COMPETITION_CATEGORY_REGISTRATION.COMPETITION_CATEGORY_ID))
           .join(COMPETITION).on(COMPETITION.ID.eq(COMPETITION_CATEGORY.COMPETITION_ID))
           .where(COMPETITION.ID.eq(competitionId))
           .fetchInto(CLUB)

        return clubRecords.map { it.toDto() }
    }

    private fun ClubRecord.toDto(): ClubDTO {
        return ClubDTO(this.id, this.name, this.address)
    }

    /**
     * WARNING: Only use this in test code
     */
    internal fun clearClubTable() = dslContext.deleteFrom(CLUB).execute()
}