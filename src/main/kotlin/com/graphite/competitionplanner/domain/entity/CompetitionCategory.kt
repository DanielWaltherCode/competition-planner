package com.graphite.competitionplanner.domain.entity

import com.graphite.competitionplanner.competitioncategory.domain.interfaces.CompetitionCategoryDTO

data class CompetitionCategory(
    val id: Int,
    val status: CompetitionCategoryStatus,
    val category: Category,
    val settings: GeneralSettings,
    val gameSettings: GameSettings
) {
    constructor(dto: CompetitionCategoryDTO) : this(
        dto.id,
        CompetitionCategoryStatus.valueOf(dto.status),
        Category(dto.category),
        GeneralSettings(dto.settings),
        GameSettings(dto.gameSettings)
    )
}
