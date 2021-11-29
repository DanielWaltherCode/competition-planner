package com.graphite.competitionplanner.billing.interfaces

import com.graphite.competitionplanner.club.interfaces.ClubDTO

data class CostSummaryListDTO(
    val club: ClubDTO,
    val costSummaries: List<CostSummaryDTO>,
    val totalCostForClub: Float
) {
}