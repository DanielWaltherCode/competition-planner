package com.graphite.competitionplanner.competitioncategory.domain.interfaces

data class CompetitionCategoryUpdateDTO(
    val id: Int,
    val settings: GeneralSettingsDTO,
    val gameSettings: GameSettingsDTO
)