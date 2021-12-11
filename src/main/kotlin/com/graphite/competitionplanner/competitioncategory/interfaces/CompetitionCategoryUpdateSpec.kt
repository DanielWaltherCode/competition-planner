package com.graphite.competitionplanner.competitioncategory.interfaces

data class CompetitionCategoryUpdateSpec(
    val settings: GeneralSettingsDTO,
    val gameSettings: GameSettingsDTO
)