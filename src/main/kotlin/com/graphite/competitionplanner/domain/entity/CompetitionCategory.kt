package com.graphite.competitionplanner.domain.entity

import com.graphite.competitionplanner.domain.dto.CompetitionCategoryDTO

data class CompetitionCategory(
    val id: Int,
    val category: Category,
    val settings: GeneralSettings,
    val gameSettings: GameSettings
) {
    constructor(dto: CompetitionCategoryDTO) : this(
        dto.id,
        Category(dto.category),
        GeneralSettings(dto.settings),
        GameSettings(dto.gameSettings)
    )
}
