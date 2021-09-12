package com.graphite.competitionplanner.competition.repository

import com.graphite.competitionplanner.Tables.CLUB
import com.graphite.competitionplanner.Tables.COMPETITION
import com.graphite.competitionplanner.club.interfaces.ClubDTO
import com.graphite.competitionplanner.common.exception.NotFoundException
import com.graphite.competitionplanner.competition.interfaces.*
import com.graphite.competitionplanner.tables.Club
import com.graphite.competitionplanner.tables.Competition
import com.graphite.competitionplanner.tables.records.CompetitionRecord
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
class CompetitionRepository(val dslContext: DSLContext) : ICompetitionRepository {

    internal fun addCompetitionWithId(competitionId: Int, competitionSpec: CompetitionSpec): CompetitionRecord {
        val competitionRecord = competitionSpec.toRecord()
        competitionRecord.id = competitionId
        competitionRecord.store()
        return competitionRecord
    }

    internal fun deleteCompetition(competitionId: Int): Boolean {
        val deletedRows =
            dslContext.deleteFrom(Competition.COMPETITION).where(Competition.COMPETITION.ID.eq(competitionId)).execute()
        return deletedRows >= 1
    }

    internal fun getByLocation(location: String): List<CompetitionRecord> {
        return dslContext.select()
            .from(COMPETITION)
            .where(COMPETITION.LOCATION.eq(location))
            .fetchInto(COMPETITION)
    }

    internal fun clearTable() = dslContext.deleteFrom(Competition.COMPETITION).execute()

    override fun store(spec: CompetitionSpec): CompetitionDTO {
        val record = spec.toRecord()
        record.store()
        return record.toDto()
    }

    override fun findCompetitionsThatBelongsTo(clubId: Int): List<CompetitionDTO> {
        val records = dslContext.selectFrom(COMPETITION).where(COMPETITION.ORGANIZING_CLUB.eq(clubId)).fetch()
        return records.map { it.toDto() }
    }

    override fun findCompetitions(start: LocalDate, end: LocalDate): List<CompetitionWithClubDTO> {
        val records = dslContext.select()
            .from(Competition.COMPETITION)
            .join(Club.CLUB)
            .on(Competition.COMPETITION.ORGANIZING_CLUB.eq(Club.CLUB.ID))
            .where(
                Competition.COMPETITION.START_DATE.between(start, end)
                    .or(Competition.COMPETITION.END_DATE.between(start, end))
            )
            .fetch()

        val result = mutableListOf<CompetitionWithClubDTO>()
        for (record in records) {
            val competition = record.into(COMPETITION)
            val club = record.into(CLUB)
            result.add(
                CompetitionWithClubDTO(
                    competition.id,
                    LocationDTO(competition.location),
                    competition.name,
                    competition.welcomeText,
                    ClubDTO(club.id, club.name, club.address),
                    competition.startDate,
                    competition.endDate
                )
            )
        }
        return result
    }

    @Throws(NotFoundException::class)
    override fun findById(competitionId: Int): CompetitionDTO {
        val record = dslContext.selectFrom(COMPETITION).where(COMPETITION.ID.eq(competitionId)).fetchOne()
            ?: throw NotFoundException("Competition with id $competitionId was not found.")
        return record.toDto()
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

    @Throws(NotFoundException::class)
    override fun update(id: Int, spec: CompetitionUpdateSpec): CompetitionDTO {
        val record = spec.toRecord()
        record.reset(COMPETITION.ORGANIZING_CLUB) // This make it so we do not try to update club id
        record.id = id
        val rowsUpdated = record.update()
        if (rowsUpdated < 1) {
            throw NotFoundException("Could not update. Competition with id $id not found.")
        }

        record.refresh() // Fetch club id. Without this it is null.

        return record.toDto()
    }

    private fun getCompetitionRecord(id: Int): CompetitionRecord? {
        return dslContext.selectFrom(COMPETITION).where(COMPETITION.ID.eq(id)).fetchOne()
    }

    private fun CompetitionUpdateSpec.toRecord(): CompetitionRecord {
        val record = dslContext.newRecord(Competition.COMPETITION)
        record.location = this.location.name
        record.name = this.name
        record.welcomeText = this.welcomeText
        record.startDate = this.startDate
        record.endDate = this.endDate
        return record
    }

    private fun CompetitionSpec.toRecord(): CompetitionRecord {
        val record = dslContext.newRecord(Competition.COMPETITION)
        record.location = this.location.name
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