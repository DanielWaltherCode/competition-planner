package com.graphite.competitionplanner.repositories

import com.graphite.competitionplanner.Tables.*
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
import kotlin.streams.toList

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
        } else {
            return dslContext
                .select()
                .from(COMPETITION)
                .join(CLUB)
                .on(COMPETITION.ORGANIZING_CLUB.eq(CLUB.ID))
                .where(
                    COMPETITION.START_DATE.between(weekStartDate, weekEndDate)
                        .or(COMPETITION.END_DATE.between(weekStartDate, weekEndDate))
                )
                .fetch()
        }
    }

    fun deleteCompetition(competitionId: Int): Boolean {
        val deletedRows = dslContext.deleteFrom(COMPETITION).where(COMPETITION.ID.eq(competitionId)).execute()
        return deletedRows >= 1
    }

    fun getById(competitionId: Int): Record {
        return dslContext.select().from(COMPETITION).join(CLUB).on(COMPETITION.ORGANIZING_CLUB.eq(CLUB.ID))
            .where(COMPETITION.ID.eq(competitionId)).fetchOne()
    }

    fun getByClubId(clubId: Int): List<Record> {
        return dslContext.select().from(COMPETITION).join(CLUB).on(COMPETITION.ORGANIZING_CLUB.eq(CLUB.ID))
            .where(CLUB.ID.eq(clubId)).fetch()
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

    fun getCategories(): List<PlayingCategoryRecord> {
        return dslContext.selectFrom(PLAYING_CATEGORY).fetch()
    }


    fun getByIds(ids: List<Int>): List<PlayingCategoryRecord> {
        return dslContext.select().from(PLAYING_CATEGORY).where(PLAYING_CATEGORY.ID.`in`(ids))
            .fetchInto(PLAYING_CATEGORY)
    }

    fun clearTable() = dslContext.deleteFrom(PLAYING_CATEGORY).execute()
}

/**
 * N..N table for categories at a given competition
 */
@Repository
class CompetitionAndPlayingCategoryRepository(val dslContext: DSLContext) {

    fun addCompetitionPlayingCategory(competitionId: Int, playingCategoryId: Int): Int {
        val record: CompetitionPlayingCategoryRecord = dslContext.newRecord(COMPETITION_PLAYING_CATEGORY)
        record.competitionId = competitionId
        record.playingCategory = playingCategoryId
        record.store()
        return record.id
    }

    fun getCompetitionCategories(): List<CompetitionPlayingCategoryRecord> {
        return dslContext.selectFrom(COMPETITION_PLAYING_CATEGORY).fetch()
    }

    fun getById(competitionCategoryId: Int): CompetitionCategory {
        val record: Record = dslContext.select(
            COMPETITION_PLAYING_CATEGORY.STATUS,
            PLAYING_CATEGORY.CATEGORY_NAME
        ).from(COMPETITION_PLAYING_CATEGORY)
            .join(PLAYING_CATEGORY).on(PLAYING_CATEGORY.ID.eq(COMPETITION_PLAYING_CATEGORY.PLAYING_CATEGORY))
            .where(COMPETITION_PLAYING_CATEGORY.ID.eq(competitionCategoryId))
            .fetchOne()

        return CompetitionCategory(
            competitionCategoryId,
            record.getValue(PLAYING_CATEGORY.CATEGORY_NAME),
            record.getValue(COMPETITION_PLAYING_CATEGORY.STATUS)
        )
    }

    fun getCategoriesInCompetition(competitionId: Int): List<CompetitionCategory> {
        val records: List<Record> = dslContext.select(
            COMPETITION_PLAYING_CATEGORY.ID,
            COMPETITION_PLAYING_CATEGORY.STATUS,
            PLAYING_CATEGORY.CATEGORY_NAME
        )
            .from(COMPETITION)
            .join(COMPETITION_PLAYING_CATEGORY).on(COMPETITION_PLAYING_CATEGORY.COMPETITION_ID.eq(COMPETITION.ID))
            .join(PLAYING_CATEGORY).on(PLAYING_CATEGORY.ID.eq(COMPETITION_PLAYING_CATEGORY.PLAYING_CATEGORY))
            .where(COMPETITION.ID.eq(competitionId))
            .fetch()

        return records.stream().map {
            CompetitionCategory(
                it.getValue(COMPETITION_PLAYING_CATEGORY.ID),
                it.getValue(PLAYING_CATEGORY.CATEGORY_NAME),
                it.getValue(COMPETITION_PLAYING_CATEGORY.STATUS)
            )
        }.toList()
    }

    fun getCategoriesAndPlayers(competitionId: Int): List<CategoriesAndPlayers> {
        val records: List<Record> =
            dslContext.select(COMPETITION_PLAYING_CATEGORY.ID, PLAYING_CATEGORY.CATEGORY_NAME, PLAYER.ID)
                .from(COMPETITION)
                .join(COMPETITION_PLAYING_CATEGORY).on(COMPETITION_PLAYING_CATEGORY.COMPETITION_ID.eq(COMPETITION.ID))
                .join(PLAYING_CATEGORY).on(PLAYING_CATEGORY.ID.eq(COMPETITION_PLAYING_CATEGORY.PLAYING_CATEGORY))
                .join(PLAYING_IN).on(PLAYING_IN.COMPETITION_PLAYING_CATEGORY_ID.eq(COMPETITION_PLAYING_CATEGORY.ID))
                .join(PLAYER_REGISTRATION).on(PLAYER_REGISTRATION.REGISTRATION_ID.eq(PLAYING_IN.REGISTRATION_ID))
                .join(PLAYER).on(PLAYER.ID.eq(PLAYER_REGISTRATION.PLAYER_ID))
                .where(COMPETITION.ID.eq(competitionId))
                .fetch()

        return records.stream().map {
            CategoriesAndPlayers(
                it.getValue(COMPETITION_PLAYING_CATEGORY.ID),
                it.getValue(PLAYING_CATEGORY.CATEGORY_NAME), it.getValue(PLAYER.ID)
            )
        }.toList()

    }

    // Should return competition information, disciplines
    fun getByPlayerId(playerId: Int): List<CompetitionAndCategories> {
        // Get combination of competitions and the categories the player plays in them
        val records: List<Record> = dslContext.select(
            COMPETITION_PLAYING_CATEGORY.COMPETITION_ID,
            COMPETITION_PLAYING_CATEGORY.ID,
            COMPETITION_PLAYING_CATEGORY.STATUS,
            PLAYING_CATEGORY.CATEGORY_NAME
        )
            .from(PLAYER_REGISTRATION).join(PLAYING_IN).on(PLAYING_IN.REGISTRATION_ID.eq(PLAYER_REGISTRATION.ID))
            .join(COMPETITION_PLAYING_CATEGORY)
            .on(COMPETITION_PLAYING_CATEGORY.ID.eq(PLAYING_IN.COMPETITION_PLAYING_CATEGORY_ID))
            .join(PLAYER).on(PLAYER_REGISTRATION.PLAYER_ID.eq(PLAYER.ID))
            .join(PLAYING_CATEGORY).on(PLAYING_CATEGORY.ID.eq(COMPETITION_PLAYING_CATEGORY.PLAYING_CATEGORY))
            .where(PLAYER.ID.eq(playerId))
            .fetch()

        return records.stream().map {
            CompetitionAndCategories(
                it.getValue(COMPETITION_PLAYING_CATEGORY.COMPETITION_ID), it.getValue(COMPETITION_PLAYING_CATEGORY.ID),
                it.getValue(PLAYING_CATEGORY.CATEGORY_NAME)
            )
        }.toList()
    }

    fun cancelCategoryInCompetition(playingCategoryId: Int) {
        dslContext.update(COMPETITION_PLAYING_CATEGORY)
            .set(COMPETITION_PLAYING_CATEGORY.STATUS, "CANCELLED")
            .where(COMPETITION_PLAYING_CATEGORY.ID.eq(playingCategoryId))
            .execute()
    }


    fun deleteCategoryInCompetition(playingCategoryId: Int) {
        dslContext.deleteFrom(COMPETITION_PLAYING_CATEGORY)
            .where(COMPETITION_PLAYING_CATEGORY.ID.eq(playingCategoryId))
            .execute()
    }

    fun clearTable() = dslContext.deleteFrom(COMPETITION_PLAYING_CATEGORY).execute()
}

@Repository
class CompetitionCategoryMetadata(
    val dslContext: DSLContext
) {
}

data class CategoriesAndPlayers(
    val playingCategoryId: Int,
    val categoryName: String,
    val playerId: Int
)

data class CompetitionAndCategories(
    val competitionId: Int,
    val playingCategoryId: Int,
    val categoryName: String
)

data class CompetitionCategory(
    val playingCategoryId: Int,
    val categoryName: String,
    val status: String
)
