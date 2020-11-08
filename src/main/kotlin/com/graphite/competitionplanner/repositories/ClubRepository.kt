package com.graphite.competitionplanner.repositories

import com.graphite.competitionplanner.api.ClubDTO
import com.graphite.competitionplanner.tables.Club.CLUB
import com.graphite.competitionplanner.tables.pojos.Club
import com.graphite.competitionplanner.tables.records.ClubRecord
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

@Repository
class ClubRepository(val dslContext: DSLContext) {

    fun addClub(clubDTO: ClubDTO): Club {
        val clubRecord: ClubRecord = dslContext.newRecord(CLUB)
        if (clubDTO.id != null) {
            clubRecord.id = clubDTO.id
        }
        clubRecord.name = clubDTO.name
        clubRecord.address = clubDTO.address
        clubRecord.store()

        return Club(clubRecord.id, clubRecord.name, clubRecord.address)
    }

    fun updateClub(clubDTO: ClubDTO): Club {
        val clubRecord: ClubRecord = dslContext.newRecord(CLUB)
        clubRecord.id = clubDTO.id
        clubRecord.name = clubDTO.name
        clubRecord.address = clubDTO.address
        clubRecord.store()

        return Club(clubRecord.id, clubRecord.name, clubRecord.address)
    }

    fun getAll(): List<Club> {
        return dslContext.select().from(CLUB).fetchInto(Club::class.java)
    }

    fun findByName(name: String): Club? {
        return dslContext.selectFrom(CLUB).where(CLUB.NAME.eq(name)).fetchOneInto(Club::class.java)
    }

    fun clearClubTable() = dslContext.deleteFrom(CLUB).execute()
}