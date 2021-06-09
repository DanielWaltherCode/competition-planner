package com.graphite.competitionplanner.repositories.competition

import com.graphite.competitionplanner.Tables.COMPETITION
import com.graphite.competitionplanner.api.competition.CompetitionSpec
import com.graphite.competitionplanner.domain.dto.CompetitionDTO
import com.graphite.competitionplanner.domain.dto.LocationDTO
import com.graphite.competitionplanner.domain.dto.NewCompetitionDTO
import com.graphite.competitionplanner.domain.interfaces.ICompetitionRepository
import com.graphite.competitionplanner.domain.interfaces.NotFoundException
import com.graphite.competitionplanner.tables.Club
import com.graphite.competitionplanner.tables.Competition
import com.graphite.competitionplanner.tables.records.CompetitionRecord
import org.jooq.DSLContext
import org.jooq.Record
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
class CompetitionRepository(val dslContext: DSLContext) : ICompetitionRepository {

    fun addCompetition(competitionSpec: CompetitionSpec): CompetitionRecord {
        val competitionRecord = dslContext.newRecord(Competition.COMPETITION)
        competitionRecord.location = competitionSpec.location
        competitionRecord.name = competitionSpec.name
        competitionRecord.welcomeText = competitionSpec.welcomeText
        competitionRecord.organizingClub = competitionSpec.organizingClubId
        competitionRecord.startDate = competitionSpec.startDate
        competitionRecord.endDate = competitionSpec.endDate
        competitionRecord.store()
        return competitionRecord
    }

    fun addCompetitionWithId(competitionId: Int, competitionSpec: CompetitionSpec): CompetitionRecord {
        val competitionRecord = dslContext.newRecord(Competition.COMPETITION)
        competitionRecord.id = competitionId
        competitionRecord.name = competitionSpec.name
        competitionRecord.location = competitionSpec.location
        competitionRecord.welcomeText = competitionSpec.welcomeText
        competitionRecord.organizingClub = competitionSpec.organizingClubId
        competitionRecord.startDate = competitionSpec.startDate
        competitionRecord.endDate = competitionSpec.endDate
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

    fun getByLocation(location: String): List<CompetitionRecord> {
        return dslContext.select()
            .from(COMPETITION)
            .where(COMPETITION.LOCATION.eq(location))
            .fetchInto(COMPETITION)
    }

    fun updateCompetition(competitionId: Int, competitionSpec: CompetitionSpec): CompetitionRecord {
        val competitionRecord = dslContext.newRecord(Competition.COMPETITION)
        competitionRecord.id = competitionId
        competitionRecord.name = competitionSpec.name
        competitionRecord.location = competitionSpec.location
        competitionRecord.welcomeText = competitionSpec.welcomeText
        competitionRecord.organizingClub = competitionSpec.organizingClubId
        competitionRecord.startDate = competitionSpec.startDate
        competitionRecord.endDate = competitionSpec.endDate
        competitionRecord.update()
        return competitionRecord
    }

    fun clearTable() = dslContext.deleteFrom(Competition.COMPETITION).execute()

    fun getAll(): List<Record> {
        return dslContext.select().from(Competition.COMPETITION).join(Club.CLUB).on(
            Competition.COMPETITION.ORGANIZING_CLUB.eq(
                Club.CLUB.ID
            )
        ).fetch()
    }

    override fun store(dto: NewCompetitionDTO): CompetitionDTO {
        val record = dto.toRecord()
        record.store()
        return record.toDto()
    }

    override fun findCompetitionsFor(clubId: Int): List<CompetitionDTO> {
        val records = dslContext.selectFrom(COMPETITION).where(COMPETITION.ORGANIZING_CLUB.eq(clubId)).fetch()
        return records.map { it.toDto() }
    }

    @Throws(NotFoundException::class)
    override fun delete(competitionId: Int): CompetitionDTO {
        val competition = getCompetitionRecord(competitionId)
        if (competition != null) {
            competition.delete()
            return competition.toDto()
        } else {
            throw NotFoundException("Could not delete. Competition with id $competitionId not found")
        }
    }

    private fun getCompetitionRecord(id: Int): CompetitionRecord? {
        return dslContext.selectFrom(COMPETITION).where(COMPETITION.ID.eq(id)).fetchOne()
    }

    private fun NewCompetitionDTO.toRecord(): CompetitionRecord {
        val record = dslContext.newRecord(Competition.COMPETITION)
        record.location = this.location
        record.name = this.name
        record.welcomeText = this.welcomeText
        record.organizingClub = this.organizingClubId
        record.startDate = this.startDate
        record.endDate = this.endDate
        return record
    }

    private fun CompetitionRecord.toDto(): CompetitionDTO {
        return CompetitionDTO(
            this.id,
            LocationDTO(this.location),
            this.name,
            this.welcomeText,
            this.organizingClub,
            this.startDate,
            this.endDate
        )
    }

}