package com.graphite.competitionplanner.draw.domain

import com.graphite.competitionplanner.competitioncategory.interfaces.CompetitionCategoryDTO
import com.graphite.competitionplanner.competitioncategory.interfaces.CompetitionCategoryStatus
import com.graphite.competitionplanner.competitioncategory.repository.CompetitionCategoryRepository
import com.graphite.competitionplanner.draw.interfaces.ICompetitionDrawRepository
import com.graphite.competitionplanner.draw.interfaces.RegistrationSeedDTO
import org.springframework.stereotype.Component

@Component
class ApproveSeeding(
    val drawRepository: ICompetitionDrawRepository,
    val competitionCategoryRepository: CompetitionCategoryRepository
) {

    /**
     * Approve and commit the given seeding
     */
    fun execute(competitionCategory: CompetitionCategoryDTO, seeding: List<RegistrationSeedDTO>) {
        // TODO: Implement as transaction
        drawRepository.storeSeeding(seeding)
        competitionCategoryRepository.setStatus(
            competitionCategory.id,
            CompetitionCategoryStatus.SEEDING_HAS_BEEN_COMMITTED
        )
    }
}