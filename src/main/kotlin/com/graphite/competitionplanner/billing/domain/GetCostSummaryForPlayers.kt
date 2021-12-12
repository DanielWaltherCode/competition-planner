package com.graphite.competitionplanner.billing.domain

import com.graphite.competitionplanner.billing.interfaces.PlayerCostSummaryDTO
import com.graphite.competitionplanner.billing.interfaces.PlayerCostSummaryListDTO
import com.graphite.competitionplanner.competitioncategory.domain.FindCompetitionCategory
import com.graphite.competitionplanner.competitioncategory.repository.CompetitionCategoryRepository
import com.graphite.competitionplanner.player.interfaces.PlayerDTO
import com.graphite.competitionplanner.registration.repository.RegistrationRepository
import com.graphite.competitionplanner.tables.records.PlayerRecord
import org.springframework.stereotype.Component

@Component
class GetCostSummaryForPlayers(
    val registrationRepository: RegistrationRepository,
    val competitionCategoryRepository: CompetitionCategoryRepository,
    val findCompetitionCategory: FindCompetitionCategory
) {

    fun execute(competitionId: Int, clubId: Int): PlayerCostSummaryListDTO {
        // Get all players in the competition for a given club
        val registeredPlayers: List<PlayerRecord> = registrationRepository.getRegistreredPlayersInCompetition(competitionId)
        val playersFromClub = registeredPlayers.filter { it.clubId == clubId }
        val playerDTOs = playersFromClub.map { PlayerDTO(it.id, it.firstName, it.lastName, it.clubId, it.dateOfBirth) }

        // Get registrations, and add player, the category, and the cost to a list
        val playerCostSummaryList = mutableListOf<PlayerCostSummaryDTO>()
        for (player in playerDTOs) {
            val playerRegistrationLists =
                competitionCategoryRepository.getRegistrationsForPlayerInCompetition(competitionId, player.id)
            for (registration in playerRegistrationLists) {
                val category = findCompetitionCategory.byId(registration.categoryId)
                val playerCostSummaryDTO = PlayerCostSummaryDTO(
                    player,
                    category,
                    if (category.category.type.equals("singles", ignoreCase = true)) category.settings.cost else category.settings.cost * 0.5f
                )
                playerCostSummaryList.add(playerCostSummaryDTO)
            }
        }
        val totalCost = playerCostSummaryList
            .map { it.price }
            .sum()
        return  PlayerCostSummaryListDTO(playerCostSummaryList, totalCost)
    }
}