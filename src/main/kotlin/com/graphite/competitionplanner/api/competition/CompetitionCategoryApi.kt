package com.graphite.competitionplanner.api.competition

import com.graphite.competitionplanner.service.*
import com.graphite.competitionplanner.service.competition.CompetitionCategoryService
import com.graphite.competitionplanner.service.competition.CompetitionService
import com.graphite.competitionplanner.service.draw.DrawStrategy
import com.graphite.competitionplanner.service.draw.DrawType
import com.graphite.competitionplanner.service.draw.Round
import org.springframework.web.bind.annotation.*

/* Handle categories in competitions */
@RestController
@RequestMapping("/competition/{competitionId}/category")
class CompetitionCategoryApi(
    val competitionService: CompetitionService,
    val competitionCategoryService: CompetitionCategoryService
) {

    @PostMapping("/{categoryId}")
    fun addCategoryToCompetition(@PathVariable competitionId: Int, @PathVariable categoryId: Int): CompetitionCategoryDTO {
        // TODO: We need to guard against a user adding same category to same competition twice to avoid
        // TODO: foreign key crash in database
        return competitionCategoryService.addCompetitionCategory(
            competitionId,
            categoryId
        )
    }

    @GetMapping
    fun getCompetitionCategories(@PathVariable competitionId: Int): List<CompetitionCategoryDTO> {
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
    val categoryService: CategoryService
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
        return CategoryMetadataPossibleValuesDTO(
            DrawType.values().asList(),
            DrawStrategy.values().asList()
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
    val drawType: DrawType,
    val nrPlayersPerGroup: Int,
    val nrPlayersToPlayoff: Int,
    val poolDrawStrategy: DrawStrategy
)

data class CategoryGameRulesSpec(
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

data class CategoryMetadataPossibleValuesDTO(
    val drawTypes: List<DrawType>,
    val drawStrategies: List<DrawStrategy>
)