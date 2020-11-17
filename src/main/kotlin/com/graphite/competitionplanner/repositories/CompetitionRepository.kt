package com.graphite.competitionplanner.repositories

import com.graphite.competitionplanner.service.CompetitionDTO
import com.graphite.competitionplanner.tables.Club.CLUB
import com.graphite.competitionplanner.tables.Competition.COMPETITION
import com.graphite.competitionplanner.tables.CompetitionPlayingCategory.COMPETITION_PLAYING_CATEGORY
import com.graphite.competitionplanner.tables.PlayingCategory.PLAYING_CATEGORY
import com.graphite.competitionplanner.tables.records.CompetitionPlayingCategoryRecord
import com.graphite.competitionplanner.tables.records.CompetitionRecord
import com.graphite.competitionplanner.tables.records.PlayingCategoryRecord
import org.jooq.DSLContext
import org.jooq.Record
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
class CompetitionRepository(val dslContext: DSLContext) {

    fun addCompetition(competitionDTO: CompetitionDTO): CompetitionRecord {
        val competitionRecord = dslContext.newRecord(COMPETITION)
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
                    .from(COMPETITION)
                    .join(CLUB)
                    .on(COMPETITION.ORGANIZING_CLUB.eq(CLUB.ID))
                    .limit(10)
                    .fetch()
        }
        else {
            return dslContext
                    .select()
                    .from(COMPETITION)
                    .join(CLUB)
                    .on(COMPETITION.ORGANIZING_CLUB.eq(CLUB.ID))
                    .where(COMPETITION.START_DATE.between(weekStartDate, weekEndDate).or(COMPETITION.END_DATE.between(weekStartDate, weekEndDate)))
                    .fetch()
        }
    }

    fun deleteCompetition(competitionId: Int): Boolean {
       val deletedRows = dslContext.deleteFrom(COMPETITION).where(COMPETITION.ID.eq(competitionId)).execute()
        return deletedRows >= 1
    }

    fun getById(competitionId: Int): Record {
        return dslContext.select().from(COMPETITION).join(CLUB).on(COMPETITION.ORGANIZING_CLUB.eq(CLUB.ID)).where(COMPETITION.ID.eq(competitionId)).fetchOne()
    }

    fun getByClubId(clubId: Int): List<Record> {
        return dslContext.select().from(COMPETITION).join(CLUB).on(COMPETITION.ORGANIZING_CLUB.eq(CLUB.ID)).where(CLUB.ID.eq(clubId)).fetch()
    }

    fun updateCompetition(competitionDTO: CompetitionDTO): CompetitionRecord {
        val competitionRecord = dslContext.newRecord(COMPETITION)
        competitionRecord.id = competitionDTO.id
        competitionRecord.location = competitionDTO.location
        competitionRecord.welcomeText = competitionDTO.welcomeText
        competitionRecord.organizingClub = competitionDTO.organizingClub.id
        competitionRecord.startDate = competitionDTO.startDate
        competitionRecord.endDate = competitionDTO.endDate
        competitionRecord.update()
        return competitionRecord
    }

    fun clearTable() = dslContext.deleteFrom(COMPETITION).execute()

    fun getAll(): List<Record> {
        return dslContext.select().from(COMPETITION).join(CLUB).on(COMPETITION.ORGANIZING_CLUB.eq(CLUB.ID)).fetch()
    }
}

/**
 * The categories possible for players to compete in. Should be a complete, non-updatable list!
 */
@Repository
class PlayingCategoryRepository(val dslContext: DSLContext) {

    fun addPlayingCategory(playingCategory: String) {
        dslContext.insertInto(PLAYING_CATEGORY).columns(PLAYING_CATEGORY.CATEGORY_NAME)
                .values(playingCategory).execute()
    }

    fun getByName(name: String): PlayingCategoryRecord {
        return dslContext.selectFrom(PLAYING_CATEGORY).where(PLAYING_CATEGORY.CATEGORY_NAME.eq(name)).fetchOne()

    }

    fun clearTable() = dslContext.deleteFrom(PLAYING_CATEGORY).execute()
}

/**
 * N..N table for categories at a given competition
 */
@Repository
class CompetitionAndPlayingCategoryRepository(val dslContext: DSLContext) {

    fun addCompetitionPlayingCategory(competitionId: Int, playingCategoryId: Int) {
        val record: CompetitionPlayingCategoryRecord = dslContext.newRecord(COMPETITION_PLAYING_CATEGORY)
        record.competitionId = competitionId
        record.playingCategory = playingCategoryId
        record.store()
    }

    fun getCompetitionCategories(): List<CompetitionPlayingCategoryRecord> {
        return dslContext.selectFrom(COMPETITION_PLAYING_CATEGORY).fetch()
    }

    fun clearTable() = dslContext.deleteFrom(COMPETITION_PLAYING_CATEGORY).execute()
}

