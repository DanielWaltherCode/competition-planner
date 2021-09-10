package com.graphite.competitionplanner.repositories

import com.graphite.competitionplanner.Tables.*
import com.graphite.competitionplanner.competitioncategory.api.CategoryGameRulesSpec
import com.graphite.competitionplanner.competitioncategory.api.CategoryMetadataSpec
import com.graphite.competitionplanner.tables.records.CompetitionCategoryGameRulesRecord
import com.graphite.competitionplanner.tables.records.CompetitionCategoryMetadataRecord
import org.jooq.DSLContext
import org.springframework.dao.DuplicateKeyException
import org.springframework.stereotype.Repository

@Repository
class CategoryMetadataRepository(val dslContext: DSLContext) {

    fun getByCategoryId(competitionCategoryId: Int): CompetitionCategoryMetadataRecord {
        return dslContext.selectFrom(COMPETITION_CATEGORY_METADATA)
            .where(COMPETITION_CATEGORY_METADATA.COMPETITION_CATEGORY_ID.eq(competitionCategoryId))
            .fetchOne()
    }

    fun addCategoryMetadata(competitionCategoryId: Int,
                            categoryMetadataSpec: CategoryMetadataSpec
    ): CompetitionCategoryMetadataRecord {
        return storeCategoryMetadata(competitionCategoryId, null, categoryMetadataSpec)
    }

    fun updateCategoryMetadata(
        categoryId: Int,
        categoryMetadataId: Int,
        categoryMetadataSpec: CategoryMetadataSpec
    ): CompetitionCategoryMetadataRecord {
        return storeCategoryMetadata(categoryId, categoryMetadataId, categoryMetadataSpec)
    }

    private fun storeCategoryMetadata(
        competitionCategoryId: Int,
        categoryMetadataId: Int?,
        categoryMetadataSpec: CategoryMetadataSpec
    ): CompetitionCategoryMetadataRecord {
        var categoryRecord = dslContext.newRecord(COMPETITION_CATEGORY_METADATA)

        categoryRecord.competitionCategoryId = competitionCategoryId
        categoryRecord.cost = categoryMetadataSpec.cost
        categoryRecord.drawType = categoryMetadataSpec.drawType.toString()
        categoryRecord.nrPlayersPerGroup = categoryMetadataSpec.nrPlayersPerGroup
        categoryRecord.nrPlayersToPlayoff = categoryMetadataSpec.nrPlayersToPlayoff
        categoryRecord.poolDrawStrategy = categoryMetadataSpec.poolDrawStrategy.toString()

        if (categoryMetadataId != null) {
            categoryRecord.id = categoryMetadataId
            categoryRecord.update()
        }
        else {
            try {
                categoryRecord.store()
            }
            catch (dke: DuplicateKeyException) {
                val storedRecord = getByCategoryId(competitionCategoryId)
                categoryRecord = updateCategoryMetadata(competitionCategoryId, storedRecord.id, categoryMetadataSpec)
            }
        }
        return categoryRecord
    }
}


@Repository
class CategoryGameRulesRepository(val dslContext: DSLContext) {

    fun getGameRules(competitionCategoryId: Int): CompetitionCategoryGameRulesRecord {
        return dslContext.selectFrom(COMPETITION_CATEGORY_GAME_RULES)
            .where(COMPETITION_CATEGORY_GAME_RULES.COMPETITION_CATEGORY_ID.eq(competitionCategoryId))
            .fetchOne()
    }

    fun addGameRules(competitionCategoryId: Int, gameRulesSpec: CategoryGameRulesSpec): CompetitionCategoryGameRulesRecord {
        var record = dslContext.newRecord(COMPETITION_CATEGORY_GAME_RULES)
        record.competitionCategoryId = competitionCategoryId
        record.nrSets = gameRulesSpec.nrSets
        record.winScore = gameRulesSpec.winScore
        record.winMargin = gameRulesSpec.winMargin
        record.differentNumberOfGamesFromRound = gameRulesSpec.differentNumberOfGamesFromRound.toString()
        record.nrSetsFinal = gameRulesSpec.nrSetsFinal
        record.winScoreFinal = gameRulesSpec.winScoreFinal
        record.winMarginFinal = gameRulesSpec.winMarginFinal
        record.tieBreakInFinalGame = gameRulesSpec.tiebreakInFinalGame
        record.winScoreTiebreak = gameRulesSpec.winScoreTiebreak
        record.winMarginTieBreak = gameRulesSpec.winMarginTiebreak
        try {
            record.store()
        }
        catch (dke: DuplicateKeyException) {
            val storedRecord = getGameRules(competitionCategoryId)
            record = updateGameRules(storedRecord.id, storedRecord.competitionCategoryId, gameRulesSpec)
        }
        return record
    }

    fun updateGameRules(gameRulesId: Int, competitionCategoryId: Int, gameRulesSpec: CategoryGameRulesSpec): CompetitionCategoryGameRulesRecord {
        val record = dslContext.newRecord(COMPETITION_CATEGORY_GAME_RULES)
        record.id = gameRulesId
        record.competitionCategoryId = competitionCategoryId
        record.nrSets = gameRulesSpec.nrSets
        record.winScore = gameRulesSpec.winScore
        record.winMargin = gameRulesSpec.winMargin
        record.differentNumberOfGamesFromRound = gameRulesSpec.differentNumberOfGamesFromRound.toString()
        record.nrSetsFinal = gameRulesSpec.nrSetsFinal
        record.winScoreFinal = gameRulesSpec.winScoreFinal
        record.winMarginFinal = gameRulesSpec.winMarginFinal
        record.tieBreakInFinalGame = gameRulesSpec.tiebreakInFinalGame
        record.winScoreTiebreak = gameRulesSpec.winScoreTiebreak
        record.winMarginTieBreak = gameRulesSpec.winMarginTiebreak
        record.update()
        return record
    }

}
