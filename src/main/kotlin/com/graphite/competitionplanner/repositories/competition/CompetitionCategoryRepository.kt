package com.graphite.competitionplanner.repositories.competition

import com.graphite.competitionplanner.Tables
import com.graphite.competitionplanner.Tables.*
import com.graphite.competitionplanner.domain.dto.*
import com.graphite.competitionplanner.domain.interfaces.ICompetitionCategoryRepository
import com.graphite.competitionplanner.tables.Competition
import com.graphite.competitionplanner.tables.records.CategoryRecord
import com.graphite.competitionplanner.tables.records.CompetitionCategoryRecord
import com.graphite.competitionplanner.tables.records.DrawTypeRecord
import com.graphite.competitionplanner.tables.records.PoolDrawStrategyRecord
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

    fun addCategoryWithId(id: Int, category: String, type: String) {
        dslContext.insertInto(CATEGORY).columns(CATEGORY.ID, CATEGORY.CATEGORY_NAME, CATEGORY.CATEGORY_TYPE)
            .values(id, category, type).execute()
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
class CompetitionCategoryRepository(val dslContext: DSLContext) : ICompetitionCategoryRepository {

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
    // Returns registrations ids for the players registered in a given competition category
    fun getRegistrationsInCategory(categoryId: Int): List<Int> {
        return dslContext
            .select(COMPETITION_CATEGORY_REGISTRATION.REGISTRATION_ID)
            .from(COMPETITION_CATEGORY_REGISTRATION)
            .where(COMPETITION_CATEGORY_REGISTRATION.COMPETITION_CATEGORY_ID.eq(categoryId))
            .fetchInto(Int::class.java)
    }

    // Should return competition information, disciplines
    fun getByPlayerId(playerId: Int): List<CompetitionAndCategories> {
        // Get combination of competitions and the categories the player plays in them
        val records = dslContext.select(
            COMPETITION_CATEGORY.COMPETITION_ID,
            COMPETITION_CATEGORY.ID,
            COMPETITION_CATEGORY.STATUS,
            CATEGORY.CATEGORY_NAME
        )
            .from(PLAYER_REGISTRATION)
                .join(COMPETITION_CATEGORY_REGISTRATION).on(COMPETITION_CATEGORY_REGISTRATION.REGISTRATION_ID.eq(PLAYER_REGISTRATION.REGISTRATION_ID))
            .join(COMPETITION_CATEGORY)
                .on(COMPETITION_CATEGORY.ID.eq(COMPETITION_CATEGORY_REGISTRATION.COMPETITION_CATEGORY_ID))
            .join(PLAYER).on(PLAYER_REGISTRATION.PLAYER_ID.eq(PLAYER.ID))
            .join(CATEGORY).on(CATEGORY.ID.eq(COMPETITION_CATEGORY.CATEGORY))
            .where(PLAYER_REGISTRATION.PLAYER_ID.eq(playerId))
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

    override fun getCompetitionCategoriesIn(competitionId: Int): List<CompetitionCategoryDTO> {
        val records = dslContext.select()
            .from(COMPETITION_CATEGORY)
            .join(CATEGORY).on(COMPETITION_CATEGORY.CATEGORY.eq(CATEGORY.ID))
            .join(COMPETITION_CATEGORY_METADATA)
            .on(COMPETITION_CATEGORY.ID.eq(COMPETITION_CATEGORY_METADATA.COMPETITION_CATEGORY_ID))
            .join(COMPETITION_CATEGORY_GAME_RULES)
            .on(COMPETITION_CATEGORY.ID.eq(COMPETITION_CATEGORY_GAME_RULES.COMPETITION_CATEGORY_ID))
            .join(DRAW_TYPE).on(COMPETITION_CATEGORY_METADATA.DRAW_TYPE_ID.eq(DRAW_TYPE.ID))
            .join(POOL_DRAW_STRATEGY).on(COMPETITION_CATEGORY_METADATA.POOL_DRAW_STRATEGY_ID.eq(POOL_DRAW_STRATEGY.ID))
            .where(COMPETITION_CATEGORY.COMPETITION_ID.eq(competitionId))
        return records.map {
            CompetitionCategoryDTO(
                it.getValue(COMPETITION_CATEGORY.ID),
                CategoryDTO(
                    it.getValue(CATEGORY.ID),
                    it.getValue(CATEGORY.CATEGORY_NAME)
                ),
                GeneralSettingsDTO(
                    it.getValue(COMPETITION_CATEGORY_METADATA.COST),
                    DrawTypeDTO(
                        it.getValue(DRAW_TYPE.ID),
                        it.getValue(DRAW_TYPE.NAME)
                    ),
                    it.getValue(COMPETITION_CATEGORY_METADATA.NR_PLAYERS_PER_GROUP),
                    it.getValue(COMPETITION_CATEGORY_METADATA.NR_PLAYERS_TO_PLAYOFF),
                    PoolDrawStrategyDTO(
                        it.getValue(POOL_DRAW_STRATEGY.ID),
                        it.getValue(POOL_DRAW_STRATEGY.NAME)
                    )
                ),
                GameSettingsDTO(
                    it.getValue(COMPETITION_CATEGORY_GAME_RULES.NR_SETS),
                    it.getValue(COMPETITION_CATEGORY_GAME_RULES.WIN_SCORE),
                    it.getValue(COMPETITION_CATEGORY_GAME_RULES.WIN_MARGIN),
                    it.getValue(COMPETITION_CATEGORY_GAME_RULES.NR_SETS_FINAL),
                    it.getValue(COMPETITION_CATEGORY_GAME_RULES.WIN_SCORE_FINAL),
                    it.getValue(COMPETITION_CATEGORY_GAME_RULES.WIN_MARGIN_FINAL),
                    it.getValue(COMPETITION_CATEGORY_GAME_RULES.WIN_SCORE_TIEBREAK),
                    it.getValue(COMPETITION_CATEGORY_GAME_RULES.WIN_MARGIN_TIE_BREAK)
                )
            )
        }
    }

    override fun addCompetitionCategoryTo(competitionId: Int, dto: CompetitionCategoryDTO): CompetitionCategoryDTO {
        val competitionCategoryRecord = dslContext.newRecord(COMPETITION_CATEGORY)
        competitionCategoryRecord.competitionId = competitionId
        competitionCategoryRecord.category = dto.category.id
        competitionCategoryRecord.store()

        val gameSettingsRecord = dslContext.newRecord(COMPETITION_CATEGORY_GAME_RULES)
        gameSettingsRecord.competitionCategoryId = competitionCategoryRecord.id
        gameSettingsRecord.nrSets = dto.gameSettings.numberOfSets
        gameSettingsRecord.winScore = dto.gameSettings.winScore
        gameSettingsRecord.winMargin = dto.gameSettings.winMargin
        gameSettingsRecord.nrSetsFinal = dto.gameSettings.numberOfSetsFinal
        gameSettingsRecord.winScoreFinal = dto.gameSettings.winScoreFinal
        gameSettingsRecord.winMarginFinal = dto.gameSettings.winMarginFinal
        gameSettingsRecord.winScoreTiebreak = dto.gameSettings.winScoreTiebreak
        gameSettingsRecord.winMarginTieBreak = dto.gameSettings.winMarginTieBreak
        gameSettingsRecord.store()

        val generalSettingRecord = dslContext.newRecord(COMPETITION_CATEGORY_METADATA)
        generalSettingRecord.competitionCategoryId = competitionCategoryRecord.id
        generalSettingRecord.cost = dto.settings.cost
        generalSettingRecord.drawTypeId = dto.settings.drawType.id
        generalSettingRecord.nrPlayersPerGroup = dto.settings.playersPerGroup
        generalSettingRecord.nrPlayersToPlayoff = dto.settings.playersToPlayOff
        generalSettingRecord.poolDrawStrategyId = dto.settings.poolDrawStrategy.id
        generalSettingRecord.store()

        return CompetitionCategoryDTO(competitionCategoryRecord.id, dto)
    }

    override fun deleteCompetitionCategory(competitionCategoryId: Int) {
        dslContext.delete(COMPETITION_CATEGORY_METADATA)
            .where(COMPETITION_CATEGORY_METADATA.COMPETITION_CATEGORY_ID.eq(competitionCategoryId))
            .execute()
        dslContext.delete(COMPETITION_CATEGORY_GAME_RULES)
            .where(COMPETITION_CATEGORY_GAME_RULES.COMPETITION_CATEGORY_ID.eq(competitionCategoryId))
            .execute()
        dslContext.deleteFrom(COMPETITION_CATEGORY)
            .where(COMPETITION_CATEGORY.ID.eq(competitionCategoryId))
            .execute()
    }

    override fun getAvailableCategories(): List<CategoryDTO> {
        return dslContext.selectFrom(CATEGORY).fetch().map { it.toDto() }
    }

    override fun addAvailableCategory(dto: CategoryDTO) {
        TODO("Not yet implemented")
    }

    override fun getDrawType(name: String): DrawTypeDTO {
        val record = dslContext.selectFrom(DRAW_TYPE).fetch().first { it.name == name }
        return record.toDto()
    }

    override fun getPoolDrawStrategy(name: String): PoolDrawStrategyDTO {
        val record = dslContext.selectFrom(POOL_DRAW_STRATEGY).fetch().first { it.name == name }
        return record.toDto()
    }

    private fun PoolDrawStrategyRecord.toDto(): PoolDrawStrategyDTO {
        return PoolDrawStrategyDTO(this.id, this.name)
    }

    private fun DrawTypeRecord.toDto(): DrawTypeDTO {
        return DrawTypeDTO(this.id, this.name)
    }

    private fun CategoryRecord.toDto(): CategoryDTO {
        return CategoryDTO(this.id, this.categoryName)
    }
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
