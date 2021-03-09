package com.graphite.competitionplanner.api.competition

import com.graphite.competitionplanner.repositories.DrawStrategyRepository
import com.graphite.competitionplanner.repositories.DrawTypeRepository
import com.graphite.competitionplanner.service.*
import com.graphite.competitionplanner.service.competition.CompetitionCategoryService
import com.graphite.competitionplanner.service.competition.CompetitionService
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

/* Handle categories in competitions */
@RestController
@RequestMapping("/competition/{competitionId}/category")
class CompetitionCategoryApi(
    val competitionService: CompetitionService,
    val competitionCategoryService: CompetitionCategoryService
) {
    // Handle categories in competitions
    @PostMapping("/categoryId")
    fun addCategoryToCompetition(@PathVariable competitionId: Int, @PathVariable categoryId: Int): CompetitionAndCategoriesDTO {
        competitionCategoryService.addCompetitionCategory(
            competitionId,
            categoryId
        )
        return competitionService.getCategoriesInCompetition(competitionId);
    }

    @GetMapping
    fun getCompetitionCategories(@PathVariable competitionId: Int): CompetitionAndCategoriesDTO {
        return competitionService.getCategoriesInCompetition(competitionId)
    }

    @DeleteMapping("/{competitionCategoryId}")
    fun deleteCompetitionCategory(@PathVariable competitionId: Int, @PathVariable competitionCategoryId: Int) {
        return competitionCategoryService.cancelCategoryInCompetition(competitionCategoryId)
    }

    @PutMapping("/{competitionCategoryId}/draw")
    fun setUpDraw() {

    }
}

@RestController
@RequestMapping("/competition/{competitionId}/category/metadata")
class CategoryMetadataApi(
    val categoryService: CategoryService,
    val drawTypeRepository: DrawTypeRepository,
    val drawStrategyRepository: DrawStrategyRepository
) {

    @GetMapping("/{competitionCategoryId}")
    fun getCategoryMetadata(@PathVariable competitionCategoryId: Int): CategoryMetadataDTO {
        return categoryService.getCategoryMetadata(competitionCategoryId)
    }

    @PostMapping("/{competitionCategoryId}")
    fun addCategoryMetadata(
        @PathVariable competitionCategoryId: Int,
        @RequestBody categoryMetadataSpec: CategoryMetadataSpec
    ): CategoryMetadataDTO {
        return categoryService.addCategoryMetadata(competitionCategoryId, categoryMetadataSpec)
    }

    @PutMapping("/{competitionCategoryId}/{categoryMetadataId}")
    fun updateCategoryMetadata(
        @PathVariable competitionCategoryId: Int,
        @PathVariable categoryMetadataId: Int,
        @RequestBody categoryMetadataSpec: CategoryMetadataSpec
    ): CategoryMetadataDTO {
        return categoryService.updateCategoryMetadata(
            competitionCategoryId,
            categoryMetadataId,
            categoryMetadataSpec
        )
    }

    @GetMapping("/possible-values")
    fun getPossibleCategoryMetadataValues(): CategoryMetadataPossibleValuesDTO {
        val drawTypes = drawTypeRepository.getAll()
        val drawStrategies = drawStrategyRepository.getAll()
        return CategoryMetadataPossibleValuesDTO(
            drawTypes.map { DrawTypeDTO(it.id, it.name) },
            drawStrategies.map { DrawStrategyDTO(it.id, it.name) }
        )
    }
}


@RestController
@RequestMapping("/category/game-rules")
data class CategoryGameRulesApi(val categoryService: CategoryService) {

    @GetMapping("/{competitionCategoryId}")
    fun getCategoryGameRules(@PathVariable competitionCategoryId: Int): CategoryGameRulesDTO {
        return categoryService.getCategoryGameRules(competitionCategoryId)
    }

    @PostMapping("/{competitionCategoryId}")
    fun addCategoryGameRules(
        @PathVariable competitionCategoryId: Int,
        @RequestBody gameRulesSpec: CategoryGameRulesSpec
    ): CategoryGameRulesDTO {
        return categoryService.addCategoryGameRules(competitionCategoryId, gameRulesSpec)
    }

    @PutMapping("/{competitionCategoryId}/{categoryGameRulesId}")
    fun updateCategoryGameRules(
        @PathVariable categoryGameRulesId: Int,
        @PathVariable competitionCategoryId: Int,
        @RequestBody gameRulesSpec: CategoryGameRulesSpec
    ): CategoryGameRulesDTO {
        return categoryService.updateCategoryGameRules(categoryGameRulesId, competitionCategoryId, gameRulesSpec)
    }
}


data class CategoryMetadataSpec(
    val cost: Float,
    val startTime: LocalDateTime,
    val drawTypeId: Int?,
    val nrPlayersPerGroup: Int,
    val nrPlayersToPlayoff: Int,
    val poolDrawStrategyId: Int?
)

data class CategoryGameRulesSpec(
    val nrSets: Int,
    val winScore: Int,
    val winMargin: Int,
    val nrSetsFinal: Int,
    val winScoreFinal: Int,
    val winMarginFinal: Int,
    val winScoreTiebreak: Int?,
    val winMarginTieBreak: Int?
)

data class DrawTypeDTO(
    val id: Int,
    val name: String
)

data class DrawStrategyDTO(
    val id: Int,
    val name: String
)

data class CategoryMetadataPossibleValuesDTO(
    val drawTypes: List<DrawTypeDTO>,
    val drawStrategies: List<DrawStrategyDTO>
)