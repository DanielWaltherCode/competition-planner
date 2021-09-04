package com.graphite.competitionplanner.domain.entity

data class CompetitionCategory(
    val id: Int,
    val category: Category,
    val settings: GeneralSettings,
    val gameSettings: GameSettings
)
