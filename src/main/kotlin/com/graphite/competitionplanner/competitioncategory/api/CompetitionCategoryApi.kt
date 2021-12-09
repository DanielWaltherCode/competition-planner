package com.graphite.competitionplanner.competitioncategory.api

import com.graphite.competitionplanner.category.interfaces.CategorySpec
import com.graphite.competitionplanner.competition.service.CompetitionService
import com.graphite.competitionplanner.competitioncategory.interfaces.*
import com.graphite.competitionplanner.competitioncategory.service.CompetitionCategoryService
import org.springframework.web.bind.annotation.*

/* Handle categories in competitions */
@RestController
@RequestMapping("/competition/{competitionId}/category")
class CompetitionCategoryApi(
    val competitionService: CompetitionService,
    val competitionCategoryService: CompetitionCategoryService
) {

    @PostMapping
    fun addCategoryToCompetition(
        @PathVariable competitionId: Int,
        @RequestBody spec: CategorySpec
    ): CompetitionCategoryDTO {
        return competitionCategoryService.addCompetitionCategory(competitionId, spec)
    }

    @GetMapping
    fun getCompetitionCategories(@PathVariable competitionId: Int): List<CompetitionCategoryDTO> {
        val competitionCategories = competitionCategoryService.getCompetitionCategoriesFor(competitionId)
        return competitionCategories.sortedBy { c -> c.category.name }
    }

    @DeleteMapping("/{competitionCategoryId}/cancel")
    fun cancelCompetitionCategory(@PathVariable competitionId: Int, @PathVariable competitionCategoryId: Int) {
        return competitionCategoryService.cancelCategoryInCompetition(competitionCategoryId)
    }
    @DeleteMapping("/{competitionCategoryId}")
    fun deleteCompetitionCategory(@PathVariable competitionId: Int, @PathVariable competitionCategoryId: Int) {
        return competitionCategoryService.deleteCategoryInCompetition(competitionCategoryId)
    }

    @PutMapping("/{competitionCategoryId}")
    fun updateCompetitionCategory(
        @PathVariable competitionId: Int,
        @PathVariable competitionCategoryId: Int,
        @RequestBody spec: CompetitionCategoryUpdateSpec
    ) {
        competitionCategoryService.updateCompetitionCategory(competitionCategoryId, spec)
    }

    @GetMapping("/{competitionCategoryId}/metadata")
    fun getCategoryGeneralSettings(@PathVariable competitionCategoryId: Int): GeneralSettingsSpec {
        return competitionCategoryService.getByCompetitionCategoryId(competitionCategoryId).settings
    }

    @GetMapping("/metadata/possible-values")
    fun getPossibleCategoryMetadataValues(): CategoryMetadataPossibleValuesDTO {
        return CategoryMetadataPossibleValuesDTO(
            DrawType.values().asList(),
            PoolDrawStrategy.values().asList()
        )
    }

    @GetMapping("/{competitionCategoryId}/game-rules")
    fun getCategoryGameSettings(@PathVariable competitionCategoryId: Int): GameSettingsSpec {
        return competitionCategoryService.getByCompetitionCategoryId(competitionCategoryId).gameSettings
    }
}
