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
        val clubId = clubRecord.store()

        return Club(clubId, clubRecord.name, clubRecord.address)
    }

    fun findByName(name: String): Club? {
        return dslContext.selectFrom(CLUB).where(CLUB.NAME.eq(name)).fetchOneInto(Club::class.java)
    }

    fun clearClubTable() = dslContext.deleteFrom(CLUB).execute()
}