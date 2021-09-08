package com.graphite.competitionplanner.repositories.competition

import com.graphite.competitionplanner.Tables.*
import com.graphite.competitionplanner.domain.dto.*
import com.graphite.competitionplanner.domain.entity.Round
import com.graphite.competitionplanner.domain.interfaces.ICategoryRepository
import com.graphite.competitionplanner.domain.interfaces.ICompetitionCategoryRepository
import com.graphite.competitionplanner.domain.interfaces.NotFoundException
import com.graphite.competitionplanner.tables.Competition
import com.graphite.competitionplanner.tables.records.CategoryRecord
import com.graphite.competitionplanner.tables.records.CompetitionCategoryGameRulesRecord
import com.graphite.competitionplanner.tables.records.CompetitionCategoryMetadataRecord
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

    fun clearTable() = dslContext.deleteFrom(CATEGORY).execute()
}

/**
 * N..N table for categories at a given competition
 */
@Repository
class CompetitionCategoryRepository(val dslContext: DSLContext) : ICompetitionCategoryRepository, ICategoryRepository {

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
            dslContext.select(COMPETITION_CATEGORY.ID, CATEGORY.CATEGORY_NAME, PLAYER.ID)
                .from(Competition.COMPETITION)
                .join(COMPETITION_CATEGORY).on(
                    COMPETITION_CATEGORY.COMPETITION_ID.eq(
                        Competition.COMPETITION.ID
                    )
                )
                .join(CATEGORY).on(
                    CATEGORY.ID.eq(
                        COMPETITION_CATEGORY.CATEGORY
                    )
                )
                .join(COMPETITION_CATEGORY_REGISTRATION).on(
                    COMPETITION_CATEGORY_REGISTRATION.COMPETITION_CATEGORY_ID.eq(
                        COMPETITION_CATEGORY.ID
                    )
                )
                .join(PLAYER_REGISTRATION)
                .on(PLAYER_REGISTRATION.REGISTRATION_ID.eq(COMPETITION_CATEGORY_REGISTRATION.REGISTRATION_ID))
                .join(PLAYER).on(PLAYER.ID.eq(PLAYER_REGISTRATION.PLAYER_ID))
                .where(Competition.COMPETITION.ID.eq(competitionId))
                .fetch()

        return records.stream().map {
            CategoriesAndPlayers(
                it.getValue(COMPETITION_CATEGORY.ID),
                it.getValue(CATEGORY.CATEGORY_NAME), it.getValue(PLAYER.ID)
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

    override fun getAll(competitionId: Int): List<CompetitionCategoryDTO> {
        val records = dslContext.select()
            .from(COMPETITION_CATEGORY)
            .join(CATEGORY).on(COMPETITION_CATEGORY.CATEGORY.eq(CATEGORY.ID))
            .join(COMPETITION_CATEGORY_METADATA)
            .on(COMPETITION_CATEGORY.ID.eq(COMPETITION_CATEGORY_METADATA.COMPETITION_CATEGORY_ID))
            .join(COMPETITION_CATEGORY_GAME_RULES)
            .on(COMPETITION_CATEGORY.ID.eq(COMPETITION_CATEGORY_GAME_RULES.COMPETITION_CATEGORY_ID))
            .where(COMPETITION_CATEGORY.COMPETITION_ID.eq(competitionId))
        return records.map {
            CompetitionCategoryDTO(
                it.getValue(COMPETITION_CATEGORY.ID),
                CategoryDTO(
                    it.getValue(CATEGORY.ID),
                    it.getValue(CATEGORY.CATEGORY_NAME),
                    it.getValue(CATEGORY.CATEGORY_TYPE)
                ),
                GeneralSettingsDTO(
                    it.getValue(COMPETITION_CATEGORY_METADATA.COST),
                    DrawTypeDTO(it.getValue(COMPETITION_CATEGORY_METADATA.DRAW_TYPE)),
                    it.getValue(COMPETITION_CATEGORY_METADATA.NR_PLAYERS_PER_GROUP),
                    it.getValue(COMPETITION_CATEGORY_METADATA.NR_PLAYERS_TO_PLAYOFF),
                    PoolDrawStrategyDTO(it.getValue(COMPETITION_CATEGORY_METADATA.POOL_DRAW_STRATEGY))
                ),
                GameSettingsDTO(
                    it.getValue(COMPETITION_CATEGORY_GAME_RULES.NR_SETS),
                    it.getValue(COMPETITION_CATEGORY_GAME_RULES.WIN_SCORE),
                    it.getValue(COMPETITION_CATEGORY_GAME_RULES.WIN_MARGIN),
                    Round.valueOf(it.getValue(COMPETITION_CATEGORY_GAME_RULES.DIFFERENT_NUMBER_OF_GAMES_FROM_ROUND)),
                    it.getValue(COMPETITION_CATEGORY_GAME_RULES.NR_SETS_FINAL),
                    it.getValue(COMPETITION_CATEGORY_GAME_RULES.WIN_SCORE_FINAL),
                    it.getValue(COMPETITION_CATEGORY_GAME_RULES.WIN_MARGIN_FINAL),
                    it.getValue(COMPETITION_CATEGORY_GAME_RULES.TIE_BREAK_IN_FINAL_GAME),
                    it.getValue(COMPETITION_CATEGORY_GAME_RULES.WIN_SCORE_TIEBREAK),
                    it.getValue(COMPETITION_CATEGORY_GAME_RULES.WIN_MARGIN_TIE_BREAK)
                )
            )
        }
    }

    override fun store(competitionId: Int, dto: CompetitionCategoryDTO): CompetitionCategoryDTO {
        val competitionCategoryRecord = dslContext.newRecord(COMPETITION_CATEGORY)
        competitionCategoryRecord.competitionId = competitionId
        competitionCategoryRecord.category = dto.category.id
        competitionCategoryRecord.store()

        dto.gameSettings.toRecord(competitionCategoryRecord.id).store()
        dto.settings.toRecord(competitionCategoryRecord.id).store()

        return CompetitionCategoryDTO(competitionCategoryRecord.id, dto)
    }

    @Throws(NotFoundException::class)
    override fun delete(competitionCategoryId: Int) {
        dslContext.delete(COMPETITION_CATEGORY_METADATA)
            .where(COMPETITION_CATEGORY_METADATA.COMPETITION_CATEGORY_ID.eq(competitionCategoryId))
            .execute()
        dslContext.delete(COMPETITION_CATEGORY_GAME_RULES)
            .where(COMPETITION_CATEGORY_GAME_RULES.COMPETITION_CATEGORY_ID.eq(competitionCategoryId))
            .execute()
        val deletedRows = dslContext.deleteFrom(COMPETITION_CATEGORY)
            .where(COMPETITION_CATEGORY.ID.eq(competitionCategoryId))
            .execute()
        if (deletedRows < 1) {
            throw NotFoundException("Could not delete. Competition category with id $competitionCategoryId not found.")
        }
    }

    @Throws(NotFoundException::class)
    override fun update(dto: CompetitionCategoryDTO) {
        // Note: Does not update the category as that is prohibited. User will have to delete and create new instead.

        dslContext.selectFrom(COMPETITION_CATEGORY).where(COMPETITION_CATEGORY.ID.eq(dto.id)).fetchOne()
            ?: throw NotFoundException("Could not update. Competition category with id ${dto.id} not found.")

        val gameSettingsRecord = dto.gameSettings.toRecord(dto.id)
        gameSettingsRecord.id = dslContext.selectFrom(COMPETITION_CATEGORY_GAME_RULES).where(
            COMPETITION_CATEGORY_GAME_RULES.COMPETITION_CATEGORY_ID.eq(dto.id)
        ).fetchOne().get(
            COMPETITION_CATEGORY_METADATA.ID
        )

        val settingRecord = dto.settings.toRecord(dto.id)
        settingRecord.id = dslContext.selectFrom(COMPETITION_CATEGORY_METADATA).where(
            COMPETITION_CATEGORY_METADATA.COMPETITION_CATEGORY_ID.eq(dto.id)
        ).fetchOne().get(
            COMPETITION_CATEGORY_METADATA.ID
        )

        gameSettingsRecord.update()
        settingRecord.update()
    }

    override fun getAvailableCategories(): List<CategoryDTO> {
        return dslContext.selectFrom(CATEGORY).fetch().map { it.toDto() }
    }

    override fun addAvailableCategory(dto: CategoryDTO) {
        TODO("Not yet implemented")
    }

    private fun GameSettingsDTO.toRecord(competitionCategoryId: Int): CompetitionCategoryGameRulesRecord {
        val record = dslContext.newRecord(COMPETITION_CATEGORY_GAME_RULES)
        record.let {
            it.competitionCategoryId = competitionCategoryId
            it.nrSets = this.numberOfSets
            it.winScore = this.winScore
            it.winMargin = this.winMargin
            it.differentNumberOfGamesFromRound = this.differentNumberOfGamesFromRound.name
            it.nrSetsFinal = this.numberOfSetsFinal
            it.winScoreFinal = this.winScoreFinal
            it.winMarginFinal = this.winMarginFinal
            it.tieBreakInFinalGame = this.tiebreakInFinalGame
            it.winScoreTiebreak = this.winScoreTiebreak
            it.winMarginTieBreak = this.winMarginTieBreak
        }
        return record
    }

    private fun GeneralSettingsDTO.toRecord(competitionCategoryId: Int): CompetitionCategoryMetadataRecord {
        val record = dslContext.newRecord(COMPETITION_CATEGORY_METADATA)
        record.let {
            it.competitionCategoryId = competitionCategoryId
            it.cost = this.cost
            it.drawType = this.drawType.name
            it.nrPlayersPerGroup = this.playersPerGroup
            it.nrPlayersToPlayoff = this.playersToPlayOff
            it.poolDrawStrategy = this.poolDrawStrategy.name
        }
        return record
    }

    private fun CategoryRecord.toDto(): CategoryDTO {
        return CategoryDTO(this.id, this.categoryName, this.categoryType)
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
