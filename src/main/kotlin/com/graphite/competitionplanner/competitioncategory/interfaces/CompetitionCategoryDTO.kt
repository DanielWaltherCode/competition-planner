package com.graphite.competitionplanner.competitioncategory.interfaces

import com.graphite.competitionplanner.category.interfaces.CategorySpec

data class CompetitionCategoryDTO(
    val id: Int,
    val status: String,
    val category: CategorySpec,
    val settings: GeneralSettingsSpec,
    val gameSettings: GameSettingsSpec
)