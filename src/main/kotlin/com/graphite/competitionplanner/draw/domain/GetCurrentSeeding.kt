package com.graphite.competitionplanner.draw.domain

import com.graphite.competitionplanner.competitioncategory.interfaces.CompetitionCategoryDTO
import com.graphite.competitionplanner.competitioncategory.interfaces.CompetitionCategoryStatus
import com.graphite.competitionplanner.draw.interfaces.ICompetitionDrawRepository
import com.graphite.competitionplanner.draw.interfaces.RegistrationSeedDTO
import com.graphite.competitionplanner.registration.interfaces.IRegistrationRepository
import com.graphite.competitionplanner.registration.interfaces.RegistrationRankingDTO
import org.springframework.stereotype.Component

@Component
class GetCurrentSeeding(
    val registrationRepository: IRegistrationRepository,
    val competitionDrawRepository: ICompetitionDrawRepository
) {

    /**
     * Return the current seed of the given competition category.
     */
    fun execute(competitionCategory: CompetitionCategoryDTO): List<RegistrationSeedDTO> {
        return if (competitionCategory.status == CompetitionCategoryStatus.OPEN_FOR_REGISTRATION) {
            // While the category is open for registrations, we re-calculate the seeding based of the registrations
            val drawPolicy = DrawPolicy.createDrawStrategy(competitionCategory)
            val registrationRankings: List<RegistrationRankingDTO> = registrationRepository.getRegistrationRanking(competitionCategory)
            drawPolicy.createSeed(registrationRankings)
        } else {
            // Otherwise the seeds should've been stored
            competitionDrawRepository.getSeeding(competitionCategory.id)
        }
    }
}