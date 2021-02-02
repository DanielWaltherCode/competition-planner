package com.graphite.competitionplanner.repositories.competition

import com.graphite.competitionplanner.Tables
import com.graphite.competitionplanner.Tables.*
import com.graphite.competitionplanner.tables.Competition
import com.graphite.competitionplanner.tables.records.CategoryRecord
import com.graphite.competitionplanner.tables.records.CompetitionCategoryRecord
import org.jooq.DSLContext
import org.jooq.Record
import org.springframework.stereotype.Repository
import kotlin.streams.toList

/**
 * The categories possible for players to compete in. Should be a complete, non-updatable list!
 */
@Repository
class CategoryRepository(val dslContext: DSLContext) {

    fun addCategory(category: String, type: String) {
        dslContext.insertInto(CATEGORY).columns(CATEGORY.CATEGORY_NAME, CATEGORY.CATEGORY_TYPE)
            .values(category, type).execute()
    }

    fun getByName(name: String): CategoryRecord {
        return dslContext.selectFrom(CATEGORY).where(CATEGORY.CATEGORY_NAME.eq(name)).fetchOne()
    }

    fun getById(id: Int): CategoryRecord {
        return dslContext.selectFrom(CATEGORY).where(CATEGORY.ID.eq(id)).fetchOne()
    }

    fun getCategories(): List<CategoryRecord> {
        return dslContext.selectFrom(CATEGORY).fetch()
    }


    fun getByIds(ids: List<Int>): List<CategoryRecord> {
        return dslContext.select().from(CATEGORY).where(CATEGORY.ID.`in`(ids))
            .fetchInto(CATEGORY)
    }

    fun clearTable() = dslContext.deleteFrom(CATEGORY).execute()
}

/**
 * N..N table for categories at a given competition
 */
@Repository
class CompetitionCategoryRepository(val dslContext: DSLContext) {

    fun addCompetitionCategory(competitionId: Int, categoryId: Int): Int {
        val record = dslContext.newRecord(COMPETITION_CATEGORY)
        record.competitionId = competitionId
        record.category = categoryId
        record.store()
        return record.id
    }

    fun getCompetitionCategories(): List<CompetitionCategoryRecord> {
        return dslContext.selectFrom(COMPETITION_CATEGORY).fetch()
    }

    fun getById(competitionCategoryId: Int): CompetitionCategory {
        val record: Record = dslContext.select(
            COMPETITION_CATEGORY.STATUS,
            CATEGORY.CATEGORY_NAME
        ).from(COMPETITION_CATEGORY)
            .join(CATEGORY).on(CATEGORY.ID.eq(COMPETITION_CATEGORY.CATEGORY))
            .where(COMPETITION_CATEGORY.ID.eq(competitionCategoryId))
            .fetchOne()

        return CompetitionCategory(
            competitionCategoryId,
            record.getValue(CATEGORY.CATEGORY_NAME),
            record.getValue(COMPETITION_CATEGORY.STATUS)
        )
    }

    fun getCategoriesInCompetition(competitionId: Int): List<CompetitionCategory> {
        val records: List<Record> = dslContext.select(
            COMPETITION_CATEGORY.ID,
            COMPETITION_CATEGORY.STATUS,
            CATEGORY.CATEGORY_NAME
        )
            .from(Competition.COMPETITION)
            .join(COMPETITION_CATEGORY).on(
                COMPETITION_CATEGORY.COMPETITION_ID.eq(
                    Competition.COMPETITION.ID))
            .join(CATEGORY).on(CATEGORY.ID.eq(COMPETITION_CATEGORY.CATEGORY))
            .where(Competition.COMPETITION.ID.eq(competitionId))
            .fetch()

        return records.stream().map {
            CompetitionCategory(
                it.getValue(COMPETITION_CATEGORY.ID),
                it.getValue(CATEGORY.CATEGORY_NAME),
                it.getValue(COMPETITION_CATEGORY.STATUS)
            )
        }.toList()
    }

    fun getCategoryType(competitionCategoryId: Int) : CategoryRecord {
        return dslContext.select().from(COMPETITION_CATEGORY)
                .join(CATEGORY).on(CATEGORY.ID.eq(COMPETITION_CATEGORY.CATEGORY))
                .where(COMPETITION_CATEGORY.ID.eq(competitionCategoryId))
                .fetchOneInto(CATEGORY)
    }

    fun getCategoriesAndPlayers(competitionId: Int): List<CategoriesAndPlayers> {
        val records: List<Record> =
            dslContext.select(COMPETITION_CATEGORY.ID, CATEGORY.CATEGORY_NAME, Tables.PLAYER.ID)
                .from(Competition.COMPETITION)
                .join(COMPETITION_CATEGORY).on(
                    COMPETITION_CATEGORY.COMPETITION_ID.eq(
                        Competition.COMPETITION.ID))
                .join(CATEGORY).on(
                    CATEGORY.ID.eq(
                        COMPETITION_CATEGORY.CATEGORY))
                .join(COMPETITION_CATEGORY_REGISTRATION).on(
                    COMPETITION_CATEGORY_REGISTRATION.COMPETITION_CATEGORY_ID.eq(
                        COMPETITION_CATEGORY.ID))
                .join(Tables.PLAYER_REGISTRATION).on(Tables.PLAYER_REGISTRATION.REGISTRATION_ID.eq(COMPETITION_CATEGORY_REGISTRATION.REGISTRATION_ID))
                .join(Tables.PLAYER).on(Tables.PLAYER.ID.eq(Tables.PLAYER_REGISTRATION.PLAYER_ID))
                .where(Competition.COMPETITION.ID.eq(competitionId))
                .fetch()

        return records.stream().map {
            CategoriesAndPlayers(
                it.getValue(COMPETITION_CATEGORY.ID),
                it.getValue(CATEGORY.CATEGORY_NAME), it.getValue(Tables.PLAYER.ID)
            )
        }.toList()

    }

    // Should return competition information, disciplines
    fun getByPlayerId(playerId: Int): List<CompetitionAndCategories> {
        // Get combination of competitions and the categories the player plays in them
        val records: List<Record> = dslContext.select(
            COMPETITION_CATEGORY.COMPETITION_ID,
            COMPETITION_CATEGORY.ID,
            COMPETITION_CATEGORY.STATUS,
            CATEGORY.CATEGORY_NAME
        )
            .from(Tables.PLAYER_REGISTRATION).join(COMPETITION_CATEGORY_REGISTRATION).on(COMPETITION_CATEGORY_REGISTRATION.REGISTRATION_ID.eq(Tables.PLAYER_REGISTRATION.ID))
            .join(COMPETITION_CATEGORY)
            .on(COMPETITION_CATEGORY.ID.eq(COMPETITION_CATEGORY_REGISTRATION.COMPETITION_CATEGORY_ID))
            .join(Tables.PLAYER).on(Tables.PLAYER_REGISTRATION.PLAYER_ID.eq(Tables.PLAYER.ID))
            .join(CATEGORY).on(CATEGORY.ID.eq(COMPETITION_CATEGORY.CATEGORY))
            .where(Tables.PLAYER.ID.eq(playerId))
            .fetch()

        return records.stream().map {
            CompetitionAndCategories(
                it.getValue(COMPETITION_CATEGORY.COMPETITION_ID), it.getValue(
                    COMPETITION_CATEGORY.ID),
                it.getValue(CATEGORY.CATEGORY_NAME)
            )
        }.toList()
    }

    fun cancelCategoryInCompetition(categoryId: Int) {
        dslContext.update(COMPETITION_CATEGORY)
            .set(COMPETITION_CATEGORY.STATUS, "CANCELLED")
            .where(COMPETITION_CATEGORY.ID.eq(categoryId))
            .execute()
    }


    fun deleteCategoryInCompetition(categoryId: Int) {
        dslContext.deleteFrom(COMPETITION_CATEGORY)
            .where(COMPETITION_CATEGORY.ID.eq(categoryId))
            .execute()
    }

    fun clearTable() = dslContext.deleteFrom(COMPETITION_CATEGORY).execute()
}

data class CategoriesAndPlayers(
    val categoryId: Int,
    val categoryName: String,
    val playerId: Int
)

data class CompetitionAndCategories(
    val competitionId: Int,
    val categoryId: Int,
    val categoryName: String
)

data class CompetitionCategory(
    val categoryId: Int,
    val categoryName: String,
    val status: String
)
