package com.graphite.competitionplanner.club.repository

import com.graphite.competitionplanner.domain.dto.ClubDTO
import com.graphite.competitionplanner.club.domain.interfaces.IClubRepository
import com.graphite.competitionplanner.domain.interfaces.NotFoundException
import com.graphite.competitionplanner.tables.Club.CLUB
import com.graphite.competitionplanner.tables.records.ClubRecord
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

@Repository
class ClubRepository(val dslContext: DSLContext) : IClubRepository {

    @Deprecated("Will be replaced by findById")
    fun getById(id: Int): ClubRecord? {
        return dslContext.selectFrom(CLUB).where(CLUB.ID.eq(id)).fetchOne()
    }

    override fun getAll(): List<ClubDTO> {
        val records = dslContext.select().from(CLUB).fetchInto(CLUB)
        return records.map { ClubDTO(it.id, it.name, it.address) }
    }

    override fun store(dto: ClubDTO): ClubDTO {
        val clubRecord: ClubRecord = dslContext.newRecord(CLUB)
        clubRecord.name = dto.name
        clubRecord.address = dto.address
        clubRecord.store()
        return ClubDTO(clubRecord.id, dto)
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
    override fun delete(dto: ClubDTO): ClubDTO {
        val deletedRows = dslContext.deleteFrom(CLUB).where(CLUB.ID.eq(dto.id)).execute()
        if (deletedRows >= 1) {
            return dto
        } else {
            throw NotFoundException("Could not delete. Club with id ${dto.id} not found.")
        }
    }

    @Throws(NotFoundException::class)
    override fun update(dto: ClubDTO): ClubDTO {
        val record: ClubRecord = dslContext.newRecord(CLUB)
        record.id = dto.id
        record.name = dto.name
        record.address = dto.address
        val rowsUpdated = record.update()
        if (rowsUpdated < 1) {
            throw NotFoundException("Could not update. Club with id ${dto.id} not found.")
        }
        return dto
    }

    /**
     * WARNING: Only use this in test code
     */
    fun clearClubTable() = dslContext.deleteFrom(CLUB).execute()
}