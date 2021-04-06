package com.graphite.competitionplanner.service

import com.graphite.competitionplanner.api.competition.CategoryGameRulesSpec
import com.graphite.competitionplanner.api.competition.CategoryMetadataSpec
import com.graphite.competitionplanner.api.competition.DrawTypeDTO
import com.graphite.competitionplanner.repositories.CategoryGameRulesRepository
import com.graphite.competitionplanner.repositories.CategoryMetadataRepository
import com.graphite.competitionplanner.repositories.DrawTypeRepository
import com.graphite.competitionplanner.tables.records.CompetitionCategoryGameRulesRecord
import com.graphite.competitionplanner.tables.records.CompetitionCategoryMetadataRecord
import com.graphite.competitionplanner.tables.records.DrawTypeRecord
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class CategoryService(val categoryMetadataRepository: CategoryMetadataRepository,
                      val categoryGameRulesRepository: CategoryGameRulesRepository,
val drawTypeRepository: DrawTypeRepository) {

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
            drawTypeRecordToDTO(drawTypeRepository.getById(categoryRecord.drawTypeId)),
            categoryRecord.nrPlayersPerGroup,
            categoryRecord.nrPlayersToPlayoff,
            categoryRecord.poolDrawStrategyId
        )
    }
}

fun drawTypeRecordToDTO(drawTypeRecord: DrawTypeRecord): DrawTypeDTO {
    return DrawTypeDTO(drawTypeRecord.id, drawTypeRecord.name)
}

fun gameRulesRecordToDTO(gameRulesRecord: CompetitionCategoryGameRulesRecord): CategoryGameRulesDTO {
    return CategoryGameRulesDTO(
        gameRulesRecord.id,
        gameRulesRecord.competitionCategoryId,
        gameRulesRecord.nrSets,
        gameRulesRecord.winScore,
        gameRulesRecord.winMargin,
        gameRulesRecord.nrSetsFinal,
        gameRulesRecord.winScoreFinal,
        gameRulesRecord.winMarginFinal,
        gameRulesRecord.winScoreTiebreak,
        gameRulesRecord.winMarginTieBreak
    )
}

data class CategoryMetadataDTO(
    val id: Int,
    val competitionCategoryId: Int,
    val cost: Float,
    val drawType: DrawTypeDTO,
    val nrPlayersPerGroup: Int,
    val nrPlayersToPlayoff: Int,
    val poolDrawStrategyId: Int?
)

data class CategoryGameRulesDTO(
    val id: Int,
    val competitionCategoryId: Int,
    val nrSets: Int,
    val winScore: Int,
    val winMargin: Int,
    val nrSetsFinal: Int,
    val winScoreFinal: Int,
    val winMarginFinal: Int,
    val winScoreTiebreak: Int?,
    val winMarginTieBreak: Int?
)