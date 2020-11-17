package com.graphite.competitionplanner.repositories

import com.graphite.competitionplanner.api.ClubDTO
import com.graphite.competitionplanner.tables.Club.CLUB
import com.graphite.competitionplanner.tables.records.ClubRecord
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

@Repository
class ClubRepository(val dslContext: DSLContext) {

    fun addClub(clubDTO: ClubDTO): ClubRecord {
        val clubRecord: ClubRecord = dslContext.newRecord(CLUB)
        if (clubDTO.id != null) {
            clubRecord.id = clubDTO.id
        }
        clubRecord.name = clubDTO.name
        clubRecord.address = clubDTO.address
        clubRecord.store()
        return clubRecord
    }

    fun updateClub(clubDTO: ClubDTO): ClubRecord {
        val clubRecord: ClubRecord = dslContext.newRecord(CLUB)
        clubRecord.id = clubDTO.id
        clubRecord.name = clubDTO.name
        clubRecord.address = clubDTO.address
        clubRecord.store()
        return clubRecord
    }

    fun getAll(): List<ClubRecord> {
        return dslContext.select().from(CLUB).fetchInto(CLUB)
    }

    fun findByName(name: String): ClubRecord? {
        return dslContext.selectFrom(CLUB).where(CLUB.NAME.eq(name)).fetchOne()
    }

    fun getById(id: Int): ClubRecord? {
        return dslContext.selectFrom(CLUB).where(CLUB.ID.eq(id)).fetchOne()
    }

    fun deleteClub(id: Int): Boolean {
        val deletedRows = dslContext.deleteFrom(CLUB).where(CLUB.ID.eq(id)).execute()
        return deletedRows >= 1
    }

    fun clearClubTable() = dslContext.deleteFrom(CLUB).execute()
}