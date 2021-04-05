package com.graphite.competitionplanner.repositories

import com.graphite.competitionplanner.Tables.*
import com.graphite.competitionplanner.api.competition.CategoryGameRulesSpec
import com.graphite.competitionplanner.api.competition.CategoryMetadataSpec
import com.graphite.competitionplanner.tables.records.CompetitionCategoryGameRulesRecord
import com.graphite.competitionplanner.tables.records.CompetitionCategoryMetadataRecord
import com.graphite.competitionplanner.tables.records.DrawTypeRecord
import com.graphite.competitionplanner.tables.records.PoolDrawStrategyRecord
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

    fun updateCategoryMetadata(categoryId: Int,
                               categoryMetadataId: Int,
                               categoryMetadataSpec: CategoryMetadataSpec): CompetitionCategoryMetadataRecord {
        return storeCategoryMetadata(categoryId, categoryMetadataId, categoryMetadataSpec)
    }

    private fun storeCategoryMetadata(competitionCategoryId: Int,
                                      categoryMetadataId: Int?,
                                      categoryMetadataSpec: CategoryMetadataSpec): CompetitionCategoryMetadataRecord {
        var categoryRecord = dslContext.newRecord(COMPETITION_CATEGORY_METADATA)

        categoryRecord.competitionCategoryId = competitionCategoryId
        categoryRecord.cost = categoryMetadataSpec.cost
        categoryRecord.drawTypeId = categoryMetadataSpec.drawTypeId
        categoryRecord.nrPlayersPerGroup = categoryMetadataSpec.nrPlayersPerGroup
        categoryRecord.nrPlayersToPlayoff = categoryMetadataSpec.nrPlayersToPlayoff
        categoryRecord.poolDrawStrategyId = categoryMetadataSpec.poolDrawStrategyId

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
class DrawTypeRepository(val dslContext: DSLContext) {

    fun addDrawType(name: String): DrawTypeRecord {
        val record = dslContext.newRecord(DRAW_TYPE)
        record.name = name
        record.store()
        return record
    }

    fun getAll(): List<DrawTypeRecord> {
        return dslContext.selectFrom(DRAW_TYPE).fetch()
    }

    fun getById(drawTypeId: Int): DrawTypeRecord {
       return dslContext.selectFrom(DRAW_TYPE).where(DRAW_TYPE.ID.eq(drawTypeId)).fetchOne()
    }
}

@Repository
class DrawStrategyRepository(val dslContext: DSLContext) {
    fun getAll(): List<PoolDrawStrategyRecord> {
        return dslContext.selectFrom(POOL_DRAW_STRATEGY).fetch()
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
        record.nrSetsFinal = gameRulesSpec.nrSetsFinal
        record.winScoreFinal = gameRulesSpec.winScoreFinal
        record.winMarginFinal = gameRulesSpec.winMarginFinal
        record.winScoreTiebreak = gameRulesSpec.winScoreTiebreak
        record.winMarginTieBreak = gameRulesSpec.winMarginTieBreak
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
        record.nrSetsFinal = gameRulesSpec.nrSetsFinal
        record.winScoreFinal = gameRulesSpec.winScoreFinal
        record.winMarginFinal = gameRulesSpec.winMarginFinal
        record.winScoreTiebreak = gameRulesSpec.winScoreTiebreak
        record.winMarginTieBreak = gameRulesSpec.winMarginTieBreak
        record.update()
        return record
    }

}

enum class DrawTypes {
    POOL_ONLY, CUP_ONLY, POOL_AND_CUP
}