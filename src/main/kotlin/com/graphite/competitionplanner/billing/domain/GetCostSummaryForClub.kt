package com.graphite.competitionplanner.billing.domain

import com.graphite.competitionplanner.billing.interfaces.CostSummaryDTO
import com.graphite.competitionplanner.billing.interfaces.CostSummaryListDTO
import com.graphite.competitionplanner.club.domain.FindClub
import com.graphite.competitionplanner.competitioncategory.domain.GetCompetitionCategory
import com.graphite.competitionplanner.competitioncategory.repository.CompetitionCategoryRepository
import com.graphite.competitionplanner.competitioncategory.repository.RegistrationsInCompetition
import com.graphite.competitionplanner.registration.repository.RegistrationRepository
import org.springframework.stereotype.Component

@Component
class GetCostSummaryForClub(
    val registrationRepository: RegistrationRepository,
    val competitionCategoryRepository: CompetitionCategoryRepository,
    val getCompetitionCategory: GetCompetitionCategory,
    val findClub: FindClub
) {

    fun execute(competitionId: Int, clubId: Int): CostSummaryListDTO {
        // Get all players in the competition for a given club
        val registeredPlayers = registrationRepository.getRegistreredPlayersInCompetition(competitionId)
        val playersFromClub = registeredPlayers.filter { it.clubId == clubId }

        // Get categories they were registered in
        val playerRegistrationLists = mutableListOf<List<RegistrationsInCompetition>>()
        for (player in playersFromClub) {
          playerRegistrationLists.add(
              competitionCategoryRepository.getRegistrationsForPlayerInCompetition(competitionId, player.id)
          )
        }

        // Count starts in each category. Double categories == half an occurence per player
        val startsInCategory = mutableMapOf<Int, Float>()
        for (registrationList in playerRegistrationLists) {
            for (registration in registrationList) {
                val currentCategory = registration.categoryId
                if (startsInCategory.containsKey(currentCategory)) {
                    startsInCategory[currentCategory] = startsInCategory[currentCategory]!!.plus(1f)
                }
                else {
                    startsInCategory[currentCategory] = 1f
                }
            }
        }

        // Get price per category
        val costSummaryList = mutableListOf<CostSummaryDTO>()

        for (start in startsInCategory.entries) {
            val category = getCompetitionCategory.execute(start.key)
            val pricePerPlayer = if (category.category.type.equals("singles", ignoreCase = true)) category.settings.cost else category.settings.cost * 0.5f
            costSummaryList.add(
                CostSummaryDTO(
                   category = category,
                   numberOfStarts = start.value,
                   price = pricePerPlayer ,
                   totalPrice = start.value * pricePerPlayer))
        }
        val totalCostForClub = costSummaryList.map{ it.totalPrice }.sum()
        return CostSummaryListDTO(findClub.byId(clubId), costSummaryList, totalCostForClub)
    }
}