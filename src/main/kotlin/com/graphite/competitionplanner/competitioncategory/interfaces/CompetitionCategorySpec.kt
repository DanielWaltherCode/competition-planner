package com.graphite.competitionplanner.competitioncategory.interfaces

import com.graphite.competitionplanner.category.interfaces.CategorySpec

data class CompetitionCategorySpec(
    val status: CompetitionCategoryStatus,
    val category: CategorySpec,
    val settings: GeneralSettingsDTO,
    val gameSettings: GameSettingsDTO
)