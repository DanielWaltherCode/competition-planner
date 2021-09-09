package com.graphite.competitionplanner.domain.dto

data class CompetitionCategoryDTO(
    val id: Int,
    val status: String,
    val category: CategoryDTO,
    val settings: GeneralSettingsDTO,
    val gameSettings: GameSettingsDTO
) {
    constructor(id: Int, dto: CompetitionCategoryDTO) : this(
        id,
        dto.status,
        dto.category,
        dto.settings,
        dto.gameSettings
    )
}