package com.graphite.competitionplanner.domain.dto

data class CompetitionCategoryUpdateDTO(
    val id: Int,
    val settings: GeneralSettingsDTO,
    val gameSettings: GameSettingsDTO
)