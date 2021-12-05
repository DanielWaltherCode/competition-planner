package com.graphite.competitionplanner.billing.interfaces

import com.graphite.competitionplanner.competitioncategory.interfaces.CompetitionCategoryDTO
import com.graphite.competitionplanner.player.interfaces.PlayerDTO

data class PlayerCostSummaryDTO(
    val player: PlayerDTO,
    val category: CompetitionCategoryDTO,
    val price: Float
)
