package com.graphite.competitionplanner.billing.interfaces

data class PlayerCostSummaryListDTO(
    val costSummaryList: List<PlayerCostSummaryDTO>,
    val totalPrice: Float
)