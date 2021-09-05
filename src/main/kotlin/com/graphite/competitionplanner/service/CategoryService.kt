package com.graphite.competitionplanner.service

import com.graphite.competitionplanner.api.competition.CategoryGameRulesSpec
import com.graphite.competitionplanner.api.competition.CategoryMetadataSpec
import com.graphite.competitionplanner.repositories.CategoryGameRulesRepository
import com.graphite.competitionplanner.repositories.CategoryMetadataRepository
import com.graphite.competitionplanner.service.draw.DrawStrategy
import com.graphite.competitionplanner.service.draw.DrawType
import com.graphite.competitionplanner.service.draw.Round
import com.graphite.competitionplanner.tables.records.CompetitionCategoryGameRulesRecord
import com.graphite.competitionplanner.tables.records.CompetitionCategoryMetadataRecord
import org.springframework.stereotype.Service

@Service
class CategoryService(val categoryMetadataRepository: CategoryMetadataRepository,
                      val categoryGameRulesRepository: CategoryGameRulesRepository) {

    fun addCategoryMetadata(
        competitionCategoryId: Int,
        categoryMetadataSpec: CategoryMetadataSpec
    ): CategoryMetadataDTO {
        val addedCategory = categoryMetadataRepository.addCategoryMetadata(
            competitionCategoryId, categoryMetadataSpec
        )
        return metadataRecordToDTO(addedCategory)
    }

    fun getCategoryMetadata(competitionCategoryId: Int): CategoryMetadataDTO {
        val categoryRecord = categoryMetadataRepository.getByCategoryId(competitionCategoryId)
        return metadataRecordToDTO(categoryRecord)
    }

    fun updateCategoryMetadata(
        competitionCategoryId: Int,
        categoryMetadataId: Int,
        categoryMetadataSpec: CategoryMetadataSpec
    ): CategoryMetadataDTO {
        val updatedCategoryRecord = categoryMetadataRepository.updateCategoryMetadata(
            competitionCategoryId,
            categoryMetadataId,
            categoryMetadataSpec
        )

        return metadataRecordToDTO(updatedCategoryRecord)
    }

    // Deletes are handled as an extension of the competition api -- they are either cancelled or removed.

    fun getCategoryGameRules(competitionCategoryId: Int): CategoryGameRulesDTO {
        val categoryGameRulesRecord = categoryGameRulesRepository.getGameRules(competitionCategoryId)
        return gameRulesRecordToDTO(categoryGameRulesRecord)
    }

    fun addCategoryGameRules(competitionCategoryId: Int, gameRulesSpec: CategoryGameRulesSpec): CategoryGameRulesDTO {
        val addedRecord = categoryGameRulesRepository.addGameRules(competitionCategoryId, gameRulesSpec)
        return gameRulesRecordToDTO(addedRecord)
    }

    fun updateCategoryGameRules(gameRulesId: Int, competitionCategoryId: Int, gameRulesSpec: CategoryGameRulesSpec): CategoryGameRulesDTO {
        val addedRecord = categoryGameRulesRepository.updateGameRules(gameRulesId, competitionCategoryId, gameRulesSpec)
        return gameRulesRecordToDTO(addedRecord)
    }

    fun metadataRecordToDTO(categoryRecord: CompetitionCategoryMetadataRecord): CategoryMetadataDTO {
        return CategoryMetadataDTO(
            categoryRecord.id,
            categoryRecord.competitionCategoryId,
            categoryRecord.cost,
            DrawType.valueOf(categoryRecord.drawType),
            categoryRecord.nrPlayersPerGroup,
            categoryRecord.nrPlayersToPlayoff,
            DrawStrategy.valueOf(categoryRecord.poolDrawStrategy)
        )
    }
}

fun gameRulesRecordToDTO(gameRulesRecord: CompetitionCategoryGameRulesRecord): CategoryGameRulesDTO {
    return CategoryGameRulesDTO(
        gameRulesRecord.id,
        gameRulesRecord.competitionCategoryId,
        gameRulesRecord.nrSets,
        gameRulesRecord.winScore,
        gameRulesRecord.winMargin,
        Round.valueOf(gameRulesRecord.differentNumberOfGamesFromRound),
        gameRulesRecord.nrSetsFinal,
        gameRulesRecord.winScoreFinal,
        gameRulesRecord.winMarginFinal,
        gameRulesRecord.tieBreakInFinalGame,
        gameRulesRecord.winScoreTiebreak,
        gameRulesRecord.winMarginTieBreak
    )
}

data class CategoryMetadataDTO(
    val id: Int,
    val competitionCategoryId: Int,
    val cost: Float,
    val drawType: DrawType,
    val nrPlayersPerGroup: Int,
    val nrPlayersToPlayoff: Int,
    val poolDrawStrategy: DrawStrategy
)

data class CategoryGameRulesDTO(
    val id: Int,
    val competitionCategoryId: Int,
    val nrSets: Int,
    val winScore: Int,
    val winMargin: Int,
    val differentNumberOfGamesFromRound: Round,
    val nrSetsFinal: Int,
    val winScoreFinal: Int,
    val winMarginFinal: Int,
    val tiebreakInFinalGame: Boolean,
    val winScoreTiebreak: Int?,
    val winMarginTiebreak: Int?
)