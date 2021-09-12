package com.graphite.competitionplanner.competitioncategory.interfaces

data class CompetitionCategoryUpdateSpec(
    val settings: GeneralSettingsSpec,
    val gameSettings: GameSettingsSpec
)