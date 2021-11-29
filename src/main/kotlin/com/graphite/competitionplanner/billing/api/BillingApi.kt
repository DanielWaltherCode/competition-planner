package com.graphite.competitionplanner.billing.api

import com.graphite.competitionplanner.billing.domain.CreatePDF
import com.graphite.competitionplanner.billing.domain.GetClubsInCompetition
import com.graphite.competitionplanner.billing.domain.GetCostSummaryForClub
import com.graphite.competitionplanner.billing.domain.GetCostSummaryForPlayers
import com.graphite.competitionplanner.billing.interfaces.CostSummaryListDTO
import com.graphite.competitionplanner.billing.interfaces.PlayerCostSummaryDTO
import com.graphite.competitionplanner.billing.interfaces.PlayerCostSummaryListDTO
import com.graphite.competitionplanner.club.interfaces.ClubDTO
import org.apache.pdfbox.pdmodel.PDDocument
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("billing/{competitionId}")
class BillingApi(
    val getClubsInCompetition: GetClubsInCompetition,
    val getCostSummaryForClub: GetCostSummaryForClub,
    val getCostSummaryForPlayers: GetCostSummaryForPlayers,
    val createPDF: CreatePDF
) {

    @GetMapping
    fun getParticipatingClubs(@PathVariable competitionId: Int): List<ClubDTO> {
        val clubs = getClubsInCompetition.execute(competitionId)
        return clubs.sortedBy { c -> c.name }
    }

    @GetMapping("/cost-summary/{clubId}")
    fun getCostSummaryForClub(@PathVariable competitionId: Int, @PathVariable clubId: Int): CostSummaryListDTO {
        return getCostSummaryForClub.execute(competitionId, clubId)
    }

    @GetMapping("/cost-summary/{clubId}/players")
    fun getCostSummaryForPlayersInClub(@PathVariable competitionId: Int, @PathVariable clubId: Int): PlayerCostSummaryListDTO {
        return getCostSummaryForPlayers.execute(competitionId, clubId)
    }

}