package com.graphite.competitionplanner.competitioncategory.api

import com.graphite.competitionplanner.category.interfaces.CategorySpec
import com.graphite.competitionplanner.competitioncategory.domain.*
import com.graphite.competitionplanner.competitioncategory.interfaces.*
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/competition/{competitionId}/category")
class CompetitionCategoryApi(
    val cancelCompetitionCategory: CancelCompetitionCategory,
    val findCompetitionCategory: FindCompetitionCategory,
    val getCompetitionCategories: GetCompetitionCategories,
    val updateCompetitionCategory: UpdateCompetitionCategory,
    val addCompetitionCategory: AddCompetitionCategory,
    val deleteCompetitionCategory: DeleteCompetitionCategory,
    val openForRegistrations: OpenForRegistrations,
    val closeForRegistrations: CloseForRegistrations
) {

    @PostMapping
    fun addCategoryToCompetition(
        @PathVariable competitionId: Int,
        @RequestBody spec: CategorySpec
    ): CompetitionCategoryDTO {
        return addCompetitionCategory.execute(competitionId, spec)
    }

    @GetMapping
    fun getCompetitionCategories(@PathVariable competitionId: Int): List<CompetitionCategoryDTO> {
        val competitionCategories = getCompetitionCategories.execute(competitionId)
        return competitionCategories.sortedBy { c -> c.category.name }
    }

    @DeleteMapping("/{competitionCategoryId}/cancel")
    fun cancelCompetitionCategory(@PathVariable competitionId: Int, @PathVariable competitionCategoryId: Int) {
        return cancelCompetitionCategory.execute(competitionCategoryId)
    }

    @PutMapping("/{competitionCategoryId}/open")
    fun openForRegistrations(@PathVariable competitionId: Int, @PathVariable competitionCategoryId: Int) {
        val competitionCategory = findCompetitionCategory.byId(competitionCategoryId)
        return openForRegistrations.execute(competitionCategory)
    }

    @PutMapping("/{competitionCategoryId}/close")
    fun closeForRegistrations(@PathVariable competitionId: Int, @PathVariable competitionCategoryId: Int) {
        val competitionCategory = findCompetitionCategory.byId(competitionCategoryId)
        return closeForRegistrations.execute(competitionCategory)
    }

    @DeleteMapping("/{competitionCategoryId}")
    fun deleteCompetitionCategory(@PathVariable competitionId: Int, @PathVariable competitionCategoryId: Int) {
        return deleteCompetitionCategory.execute(competitionCategoryId)
    }

    @PutMapping("/{competitionCategoryId}")
    fun updateCompetitionCategory(
        @PathVariable competitionId: Int,
        @PathVariable competitionCategoryId: Int,
        @RequestBody spec: CompetitionCategoryUpdateSpec
    ) {
        updateCompetitionCategory.execute(competitionCategoryId, spec)
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
