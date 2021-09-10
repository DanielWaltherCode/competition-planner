package com.graphite.competitionplanner.competitioncategory.domain.interfaces

import com.graphite.competitionplanner.category.domain.interfaces.CategoryDTO

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