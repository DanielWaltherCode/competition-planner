package com.graphite.competitionplanner.api.competition

import com.graphite.competitionplanner.api.CategorySpec
import com.graphite.competitionplanner.domain.dto.*
import com.graphite.competitionplanner.domain.dto.CompetitionCategoryDTO
import com.graphite.competitionplanner.domain.entity.DrawType
import com.graphite.competitionplanner.domain.entity.PoolDrawStrategy
import com.graphite.competitionplanner.service.*
import com.graphite.competitionplanner.service.competition.CompetitionCategoryService
import com.graphite.competitionplanner.service.competition.CompetitionService
import com.graphite.competitionplanner.domain.entity.Round
import org.springframework.web.bind.annotation.*

/* Handle categories in competitions */
@RestController
@RequestMapping("/competition/{competitionId}/category")
class CompetitionCategoryApi(
    val competitionService: CompetitionService,
    val competitionCategoryService: CompetitionCategoryService,
    val categoryService: CategoryService
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
            CompetitionCategoryUpdateDTO(
                spec.id,
                GeneralSettingsDTO(
                    spec.settings.cost, DrawTypeDTO(spec.settings.drawType.name), spec.settings.nrPlayersPerGroup,
                    spec.settings.nrPlayersToPlayoff, PoolDrawStrategyDTO(spec.settings.poolDrawStrategy.name)
                ),
                GameSettingsDTO(
                    spec.gameRules.nrSets,
                    spec.gameRules.winScore,
                    spec.gameRules.winMargin,
                    spec.gameRules.differentNumberOfGamesFromRound,
                    spec.gameRules.nrSetsFinal,
                    spec.gameRules.winScoreFinal,
                    spec.gameRules.winMarginFinal,
                    spec.gameRules.tiebreakInFinalGame,
                    spec.gameRules.winScoreTiebreak ?: 0,
                    spec.gameRules.winMarginTiebreak ?: 0
                )
            )
        )
    }

    @GetMapping("/{competitionCategoryId}/metadata")
    fun getCategoryMetadata(@PathVariable competitionCategoryId: Int): CategoryMetadataDTO {
        return categoryService.getCategoryMetadata(competitionCategoryId)
    }

    @GetMapping("/metadata/possible-values")
    fun getPossibleCategoryMetadataValues(): CategoryMetadataPossibleValuesDTO {
        return CategoryMetadataPossibleValuesDTO(
            DrawType.values().asList(),
            PoolDrawStrategy.values().asList()
        )
    }

    @GetMapping("/{competitionCategoryId}/game-rules")
    fun getCategoryGameRules(@PathVariable competitionCategoryId: Int): CategoryGameRulesDTO {
        return categoryService.getCategoryGameRules(competitionCategoryId)
    }
}

data class CompetitionCategorySpec(
    val id: Int,
    val category: CategorySpec,
    val settings: CategoryMetadataSpec,
    val gameRules: CategoryGameRulesSpec
) {
    constructor(dto: CompetitionCategoryDTO) : this(
        dto.id,
        CategorySpec(dto.category),
        CategoryMetadataSpec(dto.settings),
        CategoryGameRulesSpec(dto.gameSettings)
    )
}

data class CategoryMetadataSpec(
    val cost: Float,
    val drawType: DrawType,
    val nrPlayersPerGroup: Int,
    val nrPlayersToPlayoff: Int,
    val poolDrawStrategy: PoolDrawStrategy
) {
    constructor(dto: GeneralSettingsDTO) : this(
        dto.cost,
        DrawType.valueOf(dto.drawType.name),
        dto.playersPerGroup,
        dto.playersToPlayOff,
        PoolDrawStrategy.valueOf(dto.poolDrawStrategy.name)
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
        dto.differentNumberOfGamesFromRound,
        dto.numberOfSetsFinal,
        dto.winScoreFinal,
        dto.winMarginFinal,
        dto.tiebreakInFinalGame,
        dto.winScoreTiebreak,
        dto.winMarginTieBreak
    )
}

data class CategoryMetadataPossibleValuesDTO(
    val drawTypes: List<DrawType>,
    val drawStrategies: List<PoolDrawStrategy>
)