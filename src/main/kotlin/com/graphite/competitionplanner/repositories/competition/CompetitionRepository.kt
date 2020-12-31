package com.graphite.competitionplanner.repositories.competition

import com.graphite.competitionplanner.service.CompetitionDTO
import com.graphite.competitionplanner.tables.Club
import com.graphite.competitionplanner.tables.Competition
import com.graphite.competitionplanner.tables.records.CompetitionRecord
import org.jooq.DSLContext
import org.jooq.Record
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
class CompetitionRepository(val dslContext: DSLContext) {

    fun addCompetition(competitionDTO: CompetitionDTO): CompetitionRecord {
        val competitionRecord = dslContext.newRecord(Competition.COMPETITION)
        competitionRecord.location = competitionDTO.location
        competitionRecord.welcomeText = competitionDTO.welcomeText
        competitionRecord.organizingClub = competitionDTO.organizingClub.id
        competitionRecord.startDate = competitionDTO.startDate
        competitionRecord.endDate = competitionDTO.endDate
        competitionRecord.store()
        return competitionRecord
    }

    // Returns a join of competition and club
    fun getCompetitions(weekStartDate: LocalDate?, weekEndDate: LocalDate?): List<Record> {
        if (weekStartDate == null) {
            return dslContext
                .select()
                .from(Competition.COMPETITION)
                .join(Club.CLUB)
                .on(Competition.COMPETITION.ORGANIZING_CLUB.eq(Club.CLUB.ID))
                .limit(10)
                .fetch()
        } else {
            return dslContext
                .select()
                .from(Competition.COMPETITION)
                .join(Club.CLUB)
                .on(Competition.COMPETITION.ORGANIZING_CLUB.eq(Club.CLUB.ID))
                .where(
                    Competition.COMPETITION.START_DATE.between(weekStartDate, weekEndDate)
                        .or(Competition.COMPETITION.END_DATE.between(weekStartDate, weekEndDate))
                )
                .fetch()
        }
    }

    fun deleteCompetition(competitionId: Int): Boolean {
        val deletedRows = dslContext.deleteFrom(Competition.COMPETITION).where(Competition.COMPETITION.ID.eq(competitionId)).execute()
        return deletedRows >= 1
    }

    fun getById(competitionId: Int): Record {
        return dslContext.select().from(Competition.COMPETITION).join(Club.CLUB).on(
            Competition.COMPETITION.ORGANIZING_CLUB.eq(
                Club.CLUB.ID))
            .where(Competition.COMPETITION.ID.eq(competitionId)).fetchOne()
    }

    fun getByClubId(clubId: Int): List<Record> {
        return dslContext.select().from(Competition.COMPETITION).join(Club.CLUB).on(
            Competition.COMPETITION.ORGANIZING_CLUB.eq(
                Club.CLUB.ID))
            .where(Club.CLUB.ID.eq(clubId)).fetch()
    }

    fun updateCompetition(competitionDTO: CompetitionDTO): CompetitionRecord {
        val competitionRecord = dslContext.newRecord(Competition.COMPETITION)
        competitionRecord.id = competitionDTO.id
        competitionRecord.location = competitionDTO.location
        competitionRecord.welcomeText = competitionDTO.welcomeText
        competitionRecord.organizingClub = competitionDTO.organizingClub.id
        competitionRecord.startDate = competitionDTO.startDate
        competitionRecord.endDate = competitionDTO.endDate
        competitionRecord.update()
        return competitionRecord
    }

    fun clearTable() = dslContext.deleteFrom(Competition.COMPETITION).execute()

    fun getAll(): List<Record> {
        return dslContext.select().from(Competition.COMPETITION).join(Club.CLUB).on(
            Competition.COMPETITION.ORGANIZING_CLUB.eq(
                Club.CLUB.ID)).fetch()
    }

}