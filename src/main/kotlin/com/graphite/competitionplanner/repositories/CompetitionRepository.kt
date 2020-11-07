package com.graphite.competitionplanner.repositories

import com.graphite.competitionplanner.api.CompetitionDTO
import com.graphite.competitionplanner.tables.Club.CLUB
import com.graphite.competitionplanner.tables.Competition.COMPETITION
import com.graphite.competitionplanner.tables.CompetitionPlayingCategory.COMPETITION_PLAYING_CATEGORY
import com.graphite.competitionplanner.tables.PlayingCategory.PLAYING_CATEGORY
import com.graphite.competitionplanner.tables.pojos.Competition
import com.graphite.competitionplanner.tables.pojos.CompetitionPlayingCategory
import com.graphite.competitionplanner.tables.pojos.PlayingCategory
import com.graphite.competitionplanner.tables.records.CompetitionPlayingCategoryRecord
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
class CompetitionRepository(val dslContext: DSLContext) {

    fun addCompetition(competitionDTO: CompetitionDTO): Competition {
        val competitionRecord = dslContext.newRecord(COMPETITION)
        competitionRecord.location = competitionDTO.location
        competitionRecord.welcomeText = competitionDTO.welcomeText
        competitionRecord.organizingClub = competitionDTO.organizingClub
        competitionRecord.startDate = competitionDTO.startDate
        competitionRecord.endDate = competitionDTO.endDate
        competitionRecord.store()

        val competitionId: Int = competitionRecord.id
        return Competition(competitionId,
                competitionRecord.location,
                competitionRecord.welcomeText,
                competitionRecord.organizingClub,
                competitionRecord.startDate,
                competitionRecord.endDate)
    }

    fun getCompetitions(weekStartDate: LocalDate?, weekEndDate: LocalDate?): List<Competition> {
        if (weekStartDate == null) {
            return dslContext
                    .select(COMPETITION.ID, COMPETITION.ORGANIZING_CLUB, COMPETITION.LOCATION, COMPETITION.WELCOME_TEXT, COMPETITION.END_DATE, COMPETITION.START_DATE)
                    .from(COMPETITION)
                    .limit(10)
                    .fetchInto(Competition::class.java)
        }
        else {
            return dslContext
                    .select(COMPETITION.ID, COMPETITION.ORGANIZING_CLUB, COMPETITION.LOCATION, COMPETITION.WELCOME_TEXT, COMPETITION.END_DATE, COMPETITION.START_DATE)
                    .from(COMPETITION)
                    .where(COMPETITION.START_DATE.between(weekStartDate, weekEndDate).or(COMPETITION.END_DATE.between(weekStartDate, weekEndDate)))
                    .fetchInto(Competition::class.java)
        }
    }

    fun getByClubName(clubName: String): List<Competition> {
        return dslContext.select(COMPETITION.ID, COMPETITION.ORGANIZING_CLUB, COMPETITION.LOCATION, COMPETITION.WELCOME_TEXT, COMPETITION.END_DATE, COMPETITION.START_DATE)
                .from(COMPETITION)
                .join(CLUB)
                .on(COMPETITION.ORGANIZING_CLUB.eq(CLUB.ID))
                .where(CLUB.NAME.eq(clubName))
                .fetchInto(Competition::class.java)
    }

    fun deleteCompetition(competitionId: Int) {
        dslContext.deleteFrom(COMPETITION).where(COMPETITION.ID.eq(competitionId)).execute()
    }

    fun updateCompetition(competitionDTO: CompetitionDTO): Competition {
        val competitionRecord = dslContext.newRecord(COMPETITION)
        competitionRecord.id = competitionDTO.id
        competitionRecord.location = competitionDTO.location
        competitionRecord.welcomeText = competitionDTO.welcomeText
        competitionRecord.organizingClub = competitionDTO.organizingClub
        competitionRecord.startDate = competitionDTO.startDate
        competitionRecord.endDate = competitionDTO.endDate
        competitionRecord.update()

        val competitionId: Int = competitionRecord.id
        return Competition(competitionId,
                competitionRecord.location,
                competitionRecord.welcomeText,
                competitionRecord.organizingClub,
                competitionRecord.startDate,
                competitionRecord.endDate)
    }

    fun clearTable() = dslContext.deleteFrom(COMPETITION).execute()

    fun getAll(): List<Competition> {
        return dslContext.select().from(COMPETITION).fetchInto(Competition::class.java)
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

    fun getByName(name: String): PlayingCategory {
        return dslContext.selectFrom(PLAYING_CATEGORY).where(PLAYING_CATEGORY.CATEGORY_NAME.eq(name)).fetchOneInto(PlayingCategory::class.java)
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

    fun getCompetitionCategories(): List<CompetitionPlayingCategory> {
        return dslContext.selectFrom(COMPETITION_PLAYING_CATEGORY).fetchInto(CompetitionPlayingCategory::class.java)
    }

    fun clearTable() = dslContext.deleteFrom(COMPETITION_PLAYING_CATEGORY).execute()
}

