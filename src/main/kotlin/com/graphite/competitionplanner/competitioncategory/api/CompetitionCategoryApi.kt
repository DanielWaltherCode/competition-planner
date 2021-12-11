package com.graphite.competitionplanner.competitioncategory.api

import com.graphite.competitionplanner.category.interfaces.CategorySpec
import com.graphite.competitionplanner.competitioncategory.domain.CancelCompetitionCategory
import com.graphite.competitionplanner.competitioncategory.domain.FindCompetitionCategory
import com.graphite.competitionplanner.competitioncategory.interfaces.*
import com.graphite.competitionplanner.competitioncategory.service.CompetitionCategoryService
import org.springframework.web.bind.annotation.*

/* Handle categories in competitions */
@RestController
@RequestMapping("/competition/{competitionId}/category")
class CompetitionCategoryApi(
    val competitionCategoryService: CompetitionCategoryService,
    val cancelCompetitionCategory: CancelCompetitionCategory,
    val findCompetitionCategory: FindCompetitionCategory,
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
        val competitionCategory = findCompetitionCategory.byId(competitionCategoryId)
        return cancelCompetitionCategory.execute(competitionCategory)
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
    fun getCategoryGeneralSettings(@PathVariable competitionCategoryId: Int): GeneralSettingsDTO {
        return findCompetitionCategory.byId(competitionCategoryId).settings
    }

    @GetMapping("/{competitionCategoryId}/game-rules")
    fun getCategoryGameSettings(@PathVariable competitionCategoryId: Int): GameSettingsDTO {
        return findCompetitionCategory.byId(competitionCategoryId).gameSettings
    }

    @GetMapping("/metadata/possible-values")
    fun getPossibleCategoryMetadataValues(): CategoryMetadataPossibleValuesDTO {
        return CategoryMetadataPossibleValuesDTO(
            DrawType.values().asList(),
            PoolDrawStrategy.values().asList()
        )
    }
}
