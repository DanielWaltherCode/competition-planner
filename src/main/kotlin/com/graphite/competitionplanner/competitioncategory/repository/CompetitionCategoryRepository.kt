package com.graphite.competitionplanner.competitioncategory.repository

import com.graphite.competitionplanner.Tables.*
import com.graphite.competitionplanner.category.interfaces.CategorySpec
import com.graphite.competitionplanner.category.domain.CategoryType
import com.graphite.competitionplanner.common.exception.IllegalActionException
import com.graphite.competitionplanner.common.exception.NotFoundException
import com.graphite.competitionplanner.common.repository.BaseRepository
import com.graphite.competitionplanner.competitioncategory.interfaces.*
import com.graphite.competitionplanner.draw.interfaces.Round
import com.graphite.competitionplanner.registration.interfaces.PlayerRegistrationStatus
import com.graphite.competitionplanner.tables.records.CategoryRecord
import com.graphite.competitionplanner.tables.records.CompetitionCategoryGameRulesRecord
import com.graphite.competitionplanner.tables.records.CompetitionCategoryMetadataRecord
import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.SelectConditionStep
import org.springframework.stereotype.Repository

@Repository
class CompetitionCategoryRepository(
    dslContext: DSLContext
) : BaseRepository(dslContext),
    ICompetitionCategoryRepository
{

    fun getCategoryType(competitionCategoryId: Int): CategoryRecord {
        return dslContext.select().from(COMPETITION_CATEGORY)
            .join(CATEGORY).on(CATEGORY.ID.eq(COMPETITION_CATEGORY.CATEGORY))
            .where(COMPETITION_CATEGORY.ID.eq(competitionCategoryId))
            .fetchOneInto(CATEGORY)
            ?: throw NotFoundException("No competition category type found for categoryId $competitionCategoryId")

    }

    // Should return competition information, disciplines
    fun getByPlayerId(playerId: Int): List<CompetitionAndCategories> {
        // Get combination of competitions and the categories the player plays in them
        val records = dslContext.select(
            COMPETITION_CATEGORY.COMPETITION_ID,
            COMPETITION_CATEGORY.ID,
            COMPETITION_CATEGORY.STATUS,
            CATEGORY.CATEGORY_NAME,
            CATEGORY.CATEGORY_TYPE
        )
            .from(PLAYER_REGISTRATION)
            .join(COMPETITION_CATEGORY_REGISTRATION)
            .on(COMPETITION_CATEGORY_REGISTRATION.REGISTRATION_ID.eq(PLAYER_REGISTRATION.REGISTRATION_ID))
            .join(COMPETITION_CATEGORY)
            .on(COMPETITION_CATEGORY.ID.eq(COMPETITION_CATEGORY_REGISTRATION.COMPETITION_CATEGORY_ID))
            .join(PLAYER).on(PLAYER_REGISTRATION.PLAYER_ID.eq(PLAYER.ID))
            .join(CATEGORY).on(CATEGORY.ID.eq(COMPETITION_CATEGORY.CATEGORY))
            .where(PLAYER_REGISTRATION.PLAYER_ID.eq(playerId))
            .fetch()

        return records.map {
            CompetitionAndCategories(
                it.getValue(COMPETITION_CATEGORY.COMPETITION_ID),
                it.getValue(COMPETITION_CATEGORY.ID),
                it.getValue(CATEGORY.CATEGORY_NAME),
                it.getValue(CATEGORY.CATEGORY_TYPE)
            )
        }
    }

    fun getRegistrationsForPlayerInCompetition(competitionId: Int, playerId: Int): List<RegistrationsInCompetition> {
        val records = dslContext.select(
            PLAYER_REGISTRATION.REGISTRATION_ID,
            PLAYER_REGISTRATION.STATUS,
            COMPETITION_CATEGORY.ID,
            CATEGORY.CATEGORY_NAME,
            CATEGORY.CATEGORY_TYPE
        )
            .from(PLAYER_REGISTRATION)
            .join(COMPETITION_CATEGORY_REGISTRATION)
            .on(COMPETITION_CATEGORY_REGISTRATION.REGISTRATION_ID.eq(PLAYER_REGISTRATION.REGISTRATION_ID))
            .join(COMPETITION_CATEGORY)
            .on(COMPETITION_CATEGORY.ID.eq(COMPETITION_CATEGORY_REGISTRATION.COMPETITION_CATEGORY_ID))
            .join(PLAYER).on(PLAYER_REGISTRATION.PLAYER_ID.eq(PLAYER.ID))
            .join(CATEGORY).on(CATEGORY.ID.eq(COMPETITION_CATEGORY.CATEGORY))
            .where(PLAYER_REGISTRATION.PLAYER_ID.eq(playerId))
            .and(COMPETITION_CATEGORY.COMPETITION_ID.eq(competitionId))
            .fetch()

        return records.map {
            RegistrationsInCompetition(
                it.getValue(PLAYER_REGISTRATION.REGISTRATION_ID),
                it.getValue(COMPETITION_CATEGORY.ID),
                it.getValue(CATEGORY.CATEGORY_NAME),
                it.getValue(CATEGORY.CATEGORY_TYPE),
                PlayerRegistrationStatus.valueOf(it.getValue(PLAYER_REGISTRATION.STATUS))
            )
        }
    }

    /**
     * This fetches a registration (given an id) and return the player id associated with the registration, but not the
     * id passed as a parameter. The idea is that we have a player that we know are playing doubles and now we want to
     * know who they are playing with.
     */
    fun getAccompanyingPlayerId(registrationId: Int, otherPlayerId: Int): Int? {
        return dslContext.select(PLAYER_REGISTRATION.PLAYER_ID)
            .from(PLAYER_REGISTRATION)
            .where(
                PLAYER_REGISTRATION.REGISTRATION_ID.eq(registrationId)
                    .and(PLAYER_REGISTRATION.PLAYER_ID.notEqual(otherPlayerId))
            )
            .fetchOneInto(Int::class.java)
    }

    fun clearTable() = dslContext.deleteFrom(COMPETITION_CATEGORY).execute()

    @Throws(NotFoundException::class)
    override fun get(competitionCategoryID: Int): CompetitionCategoryDTO {
        val records = dslContext.select()
            .from(COMPETITION_CATEGORY)
            .join(CATEGORY).on(COMPETITION_CATEGORY.CATEGORY.eq(CATEGORY.ID))
            .join(COMPETITION_CATEGORY_METADATA)
            .on(COMPETITION_CATEGORY.ID.eq(COMPETITION_CATEGORY_METADATA.COMPETITION_CATEGORY_ID))
            .join(COMPETITION_CATEGORY_GAME_RULES)
            .on(COMPETITION_CATEGORY.ID.eq(COMPETITION_CATEGORY_GAME_RULES.COMPETITION_CATEGORY_ID))
            .where(COMPETITION_CATEGORY.ID.eq(competitionCategoryID))

        val result = toCompetitionCategoryDto(records)
        if (result.isEmpty()) {
            throw NotFoundException("Competition category with $competitionCategoryID not found.")
        } else {
            return result.first()
        }
    }

    override fun getAll(competitionId: Int): List<CompetitionCategoryDTO> {
        val records = dslContext.select()
            .from(COMPETITION_CATEGORY)
            .join(CATEGORY).on(COMPETITION_CATEGORY.CATEGORY.eq(CATEGORY.ID))
            .join(COMPETITION_CATEGORY_METADATA)
            .on(COMPETITION_CATEGORY.ID.eq(COMPETITION_CATEGORY_METADATA.COMPETITION_CATEGORY_ID))
            .join(COMPETITION_CATEGORY_GAME_RULES)
            .on(COMPETITION_CATEGORY.ID.eq(COMPETITION_CATEGORY_GAME_RULES.COMPETITION_CATEGORY_ID))
            .where(COMPETITION_CATEGORY.COMPETITION_ID.eq(competitionId))
        return toCompetitionCategoryDto(records)
    }

    override fun store(competitionId: Int, spec: CompetitionCategorySpec): CompetitionCategoryDTO {
        val competitionCategoryRecord = dslContext.newRecord(COMPETITION_CATEGORY)
        competitionCategoryRecord.status = spec.status.toString()
        competitionCategoryRecord.competitionId = competitionId
        competitionCategoryRecord.category = spec.category.id

        asTransaction {
            competitionCategoryRecord.store()
            spec.gameSettings.toRecord(competitionCategoryRecord.id).store()
            spec.settings.toRecord(competitionCategoryRecord.id).store()
        }

        return CompetitionCategoryDTO(
            id = competitionCategoryRecord.id,
            status = spec.status.name,
            category = spec.category,
            settings = spec.settings,
            gameSettings = spec.gameSettings
        )
    }

    override fun delete(competitionCategoryId: Int) {
        asTransaction {
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
    }

    @Throws(NotFoundException::class, IllegalActionException::class)
    override fun update(id: Int, spec: CompetitionCategoryUpdateSpec) {
        dslContext.selectFrom(COMPETITION_CATEGORY).where(COMPETITION_CATEGORY.ID.eq(id)).fetchOne()
            ?: throw NotFoundException("Could not update. Competition category with id $id not found.")

        val gameSettingsRecord = spec.gameSettings.toRecord(id)
        gameSettingsRecord.id = dslContext.selectFrom(COMPETITION_CATEGORY_GAME_RULES).where(
            COMPETITION_CATEGORY_GAME_RULES.COMPETITION_CATEGORY_ID.eq(id)
        ).fetchOne()?.get(
            COMPETITION_CATEGORY_METADATA.ID
        )

        val settingRecord = spec.settings.toRecord(id)
        settingRecord.id = dslContext.selectFrom(COMPETITION_CATEGORY_METADATA).where(
            COMPETITION_CATEGORY_METADATA.COMPETITION_CATEGORY_ID.eq(id)
        ).fetchOne()?.get(
            COMPETITION_CATEGORY_METADATA.ID
        )

        asTransaction {
            settingRecord.update()
            gameSettingsRecord.update()
        }
    }

    override fun setStatus(competitionCategoryId: Int, status: CompetitionCategoryStatus) {
        dslContext.update(COMPETITION_CATEGORY)
            .set(COMPETITION_CATEGORY.STATUS, status.name)
            .where(COMPETITION_CATEGORY.ID.eq(competitionCategoryId))
            .execute()
    }

    override fun getCostForCategories(categoryIds: List<Int>): MutableMap<Int, Float> {
        return dslContext
            .selectFrom(COMPETITION_CATEGORY_METADATA)
            .where(COMPETITION_CATEGORY_METADATA.COMPETITION_CATEGORY_ID.`in`(categoryIds))
            .fetchMap(COMPETITION_CATEGORY_METADATA.COMPETITION_CATEGORY_ID, COMPETITION_CATEGORY_METADATA.COST)
    }

    override fun getCategoryIds(competitionId: Int): List<Int> {
        return dslContext
                .select(COMPETITION_CATEGORY.ID)
                .from(COMPETITION_CATEGORY)
                .where(COMPETITION_CATEGORY.COMPETITION_ID.eq(competitionId))
                .fetchInto(Int::class.java)
    }

    private fun toCompetitionCategoryDto(records: SelectConditionStep<Record>): List<CompetitionCategoryDTO> {
        return records.map {
            CompetitionCategoryDTO(
                it.getValue(COMPETITION_CATEGORY.ID),
                it.getValue(COMPETITION_CATEGORY.STATUS),
                CategorySpec(
                    it.getValue(CATEGORY.ID),
                    it.getValue(CATEGORY.CATEGORY_NAME),
                    CategoryType.valueOf(it.getValue(CATEGORY.CATEGORY_TYPE))
                ),
                GeneralSettingsDTO(
                    it.getValue(COMPETITION_CATEGORY_METADATA.COST),
                    DrawType.valueOf(it.getValue(COMPETITION_CATEGORY_METADATA.DRAW_TYPE)),
                    it.getValue(COMPETITION_CATEGORY_METADATA.NR_PLAYERS_PER_GROUP),
                    it.getValue(COMPETITION_CATEGORY_METADATA.NR_PLAYERS_TO_PLAYOFF),
                    PoolDrawStrategy.valueOf(it.getValue(COMPETITION_CATEGORY_METADATA.POOL_DRAW_STRATEGY))
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
                    it.getValue(COMPETITION_CATEGORY_GAME_RULES.WIN_MARGIN_TIE_BREAK),
                    it.getValue(COMPETITION_CATEGORY_GAME_RULES.USE_DIFFERENT_RULES_IN_END_GAME)
                )
            )
        }
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
            it.useDifferentRulesInEndGame = this.useDifferentRulesInEndGame
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
}

data class CompetitionAndCategories(
    val competitionId: Int,
    val categoryId: Int,
    val categoryName: String,
    val categoryType: String
)

/**
 * Used for displaying which categories in a competition a given player is registered in
 */
data class RegistrationsInCompetition(
    val registrationId: Int,
    val categoryId: Int,
    val categoryName: String,
    val categoryType: String,
    val registrationStatus: PlayerRegistrationStatus
)

data class CompetitionCategory(
    val categoryId: Int,
    val categoryName: String,
    val status: String
)
