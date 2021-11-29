package com.graphite.competitionplanner.billing.interfaces

import com.graphite.competitionplanner.competitioncategory.interfaces.CompetitionCategoryDTO

data class CostSummaryDTO(
    val category: CompetitionCategoryDTO,
    val numberOfStarts: Float,
    val price: Float,
    val totalPrice: Float
)
