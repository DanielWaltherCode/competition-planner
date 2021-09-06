package com.graphite.competitionplanner.api.competition

import com.graphite.competitionplanner.domain.dto.*
import com.graphite.competitionplanner.domain.dto.CompetitionCategoryDTO
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

    @PostMapping()
    fun addCategoryToCompetition(
        @PathVariable competitionId: Int,
        @RequestBody category: CategorySpec
    ): CompetitionCategorySpec {
        val result = competitionCategoryService.addCompetitionCategory(
            competitionId,
            CategoryDTO(category.id, category.name, category.type)
        )

        return CompetitionCategorySpec(result)
    }

    @GetMapping
    fun getCompetitionCategories(@PathVariable competitionId: Int): List<CompetitionCategorySpec> {
        return competitionCategoryService.getCompetitionCategoriesFor(competitionId).map { CompetitionCategorySpec(it) }
    }

    @DeleteMapping("/{competitionCategoryId}")
    fun deleteCompetitionCategory(@PathVariable competitionId: Int, @PathVariable competitionCategoryId: Int) {
        return competitionCategoryService.cancelCategoryInCompetition(competitionCategoryId)
    }

    @PutMapping("/{competitionCategoryId}")
    fun updateCompetitionCategory(
        @PathVariable competitionId: Int,
        @PathVariable competitionCategoryId: Int,
        @RequestBody spec: CompetitionCategorySpec
    ) {
        competitionCategoryService.updateCompetitionCategory(
            CompetitionCategoryDTO(
                spec.id,
                CategoryDTO(spec.category.id, spec.category.name, spec.category.type),
                GeneralSettingsDTO(
                    spec.settings.cost, DrawTypeDTO(spec.settings.drawType.name), spec.settings.nrPlayersPerGroup,
                    spec.settings.nrPlayersToPlayoff, PoolDrawStrategyDTO(spec.settings.poolDrawStrategy.name)
                ),
                GameSettingsDTO(
                    spec.gameRules.nrSets, spec.gameRules.winScore, spec.gameRules.winMargin,
                    spec.gameRules.nrSetsFinal, spec.gameRules.winScoreFinal, spec.gameRules.winMarginFinal,
                    spec.gameRules.winScoreTiebreak ?: 0, spec.gameRules.winMarginTiebreak ?: 0
                )
            )
        )
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

data class CompetitionCategorySpec(
    val id: Int,
    val category: CategorySpec,
    val settings: CategoryMetadataSpec,
    val gameRules: CategoryGameRulesSpec
) {
    constructor(dto: com.graphite.competitionplanner.domain.dto.CompetitionCategoryDTO) : this(
        dto.id,
        CategorySpec(dto.category),
        CategoryMetadataSpec(dto.settings),
        CategoryGameRulesSpec(dto.gameSettings)
    )
}

data class CategorySpec(
    val id: Int,
    val name: String,
    val type: String
) {
    constructor(dto: CategoryDTO) : this(
        dto.id,
        dto.name,
        dto.type
    )
}

data class CategoryMetadataSpec(
    val cost: Float,
    val drawType: DrawType,
    val nrPlayersPerGroup: Int,
    val nrPlayersToPlayoff: Int,
    val poolDrawStrategy: DrawStrategy
) {
    constructor(dto: GeneralSettingsDTO) : this(
        dto.cost,
        DrawType.valueOf(dto.drawType.name),
        dto.playersPerGroup,
        dto.playersToPlayOff,
        DrawStrategy.valueOf(dto.poolDrawStrategy.name)
    )
}

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
) {
    constructor(dto: GameSettingsDTO) : this(
        dto.numberOfSets,
        dto.winScore,
        dto.winMargin,
        Round.ROUND_OF_16, // TODO: Add support in domain,
        dto.numberOfSetsFinal,
        dto.winScoreFinal,
        dto.winMarginFinal,
        false, // TODO: Add support in domain
        dto.winScoreTiebreak,
        dto.winMarginTieBreak
    )
}

data class CategoryMetadataPossibleValuesDTO(
    val drawTypes: List<DrawType>,
    val drawStrategies: List<DrawStrategy>
)