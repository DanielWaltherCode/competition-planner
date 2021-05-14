package com.graphite.competitionplanner.repositories

import com.graphite.competitionplanner.domain.interfaces.IClubRepository
import com.graphite.competitionplanner.domain.interfaces.NotFoundException
import com.graphite.competitionplanner.tables.Club.CLUB
import com.graphite.competitionplanner.tables.records.ClubRecord
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

@Repository
class ClubRepository(val dslContext: DSLContext) : IClubRepository {

    @Deprecated("Will be replaced with findClubByName")
    fun findByName(name: String): ClubRecord? {
        return dslContext.selectFrom(CLUB).where(CLUB.NAME.eq(name)).fetchOne()
    }

    @Deprecated("Will be replaced by findClubById")
    fun getById(id: Int): ClubRecord? {
        return dslContext.selectFrom(CLUB).where(CLUB.ID.eq(id)).fetchOne()
    }

    override fun getAll(): List<com.graphite.competitionplanner.domain.dto.ClubDTO> {
        val records = dslContext.select().from(CLUB).fetchInto(CLUB)
        return records.map { com.graphite.competitionplanner.domain.dto.ClubDTO(it.id, it.name, it.address) }
    }

    override fun store(dto: com.graphite.competitionplanner.domain.dto.ClubDTO): com.graphite.competitionplanner.domain.dto.ClubDTO {
        val clubRecord: ClubRecord = dslContext.newRecord(CLUB)
        clubRecord.name = dto.name
        clubRecord.address = dto.address
        clubRecord.store()
        return com.graphite.competitionplanner.domain.dto.ClubDTO(clubRecord.id, dto)
    }

    // TODO: Remove this and use findClubByName
    override fun doesClubExist(name: String): Boolean {
        val record = dslContext.selectFrom(CLUB).where(CLUB.NAME.eq(name)).fetchOne()
        return record != null
    }

    @Throws(NotFoundException::class)
    override fun findClubByName(name: String): com.graphite.competitionplanner.domain.dto.ClubDTO {
        val record = dslContext.selectFrom(CLUB).where(CLUB.NAME.eq(name)).fetchOne()
        if (record != null) {
            return com.graphite.competitionplanner.domain.dto.ClubDTO(record.id, record.name, record.address)
        } else {
            throw NotFoundException("Club with name $name not found")
        }
    }

    @Throws(NotFoundException::class)
    override fun findClubById(id: Int): com.graphite.competitionplanner.domain.dto.ClubDTO {
        val record = dslContext.selectFrom(CLUB).where(CLUB.ID.eq(id)).fetchOne()
        if (record != null) {
            return com.graphite.competitionplanner.domain.dto.ClubDTO(record.id, record.name, record.address)
        } else {
            throw NotFoundException("Club with $id not found.")
        }
    }

    @Throws(NotFoundException::class)
    override fun delete(dto: com.graphite.competitionplanner.domain.dto.ClubDTO): com.graphite.competitionplanner.domain.dto.ClubDTO {
        val deletedRows = dslContext.deleteFrom(CLUB).where(CLUB.ID.eq(dto.id)).execute()
        if (deletedRows >= 1) {
            return dto
        } else {
            throw NotFoundException("Could not delete. Club with id ${dto.id} not found")
        }
    }

    @Throws(NotFoundException::class)
    override fun update(dto: com.graphite.competitionplanner.domain.dto.ClubDTO): com.graphite.competitionplanner.domain.dto.ClubDTO {
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