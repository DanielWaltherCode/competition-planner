package com.graphite.competitionplanner.billing.domain

import com.graphite.competitionplanner.billing.interfaces.CostSummaryDTO
import com.graphite.competitionplanner.billing.interfaces.CostSummaryListDTO
import com.graphite.competitionplanner.category.domain.CategoryType
import com.graphite.competitionplanner.club.domain.FindClub
import com.graphite.competitionplanner.competitioncategory.domain.FindCompetitionCategory
import com.graphite.competitionplanner.competitioncategory.interfaces.CompetitionCategoryDTO
import com.graphite.competitionplanner.competitioncategory.repository.CompetitionCategoryRepository
import com.graphite.competitionplanner.competitioncategory.repository.RegistrationsInCompetition
import com.graphite.competitionplanner.registration.repository.RegistrationRepository
import com.graphite.competitionplanner.tables.records.PlayerRecord
import org.springframework.stereotype.Component

@Component
class GetCostSummaryForClub(
    val registrationRepository: RegistrationRepository,
    val competitionCategoryRepository: CompetitionCategoryRepository,
    val findCompetitionCategory: FindCompetitionCategory,
    val findClub: FindClub
) {

    fun execute(competitionId: Int, clubId: Int): CostSummaryListDTO {
        // Get all players in the competition for a given club
        val registeredPlayers: List<PlayerRecord> = registrationRepository.getRegistreredPlayersInCompetition(competitionId)
        val playersFromClub: List<PlayerRecord>  = registeredPlayers.filter { it.clubId == clubId }

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
                val currentCategory: Int = registration.categoryId
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
            val category: CompetitionCategoryDTO = findCompetitionCategory.byId(start.key)
            val pricePerPlayer = if (category.category.type == CategoryType.SINGLES) category.settings.cost else category.settings.cost * 0.5f
            costSummaryList.add(
                CostSummaryDTO(
                   category = category,
                   numberOfStarts = start.value,
                   price = pricePerPlayer ,
                   totalPrice = start.value * pricePerPlayer))
        }
        val totalCostForClub: Float = costSummaryList.map{ it.totalPrice }.sum()
        return CostSummaryListDTO(findClub.byId(clubId), costSummaryList, totalCostForClub)
    }
}