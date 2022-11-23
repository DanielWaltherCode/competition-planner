package com.graphite.competitionplanner.draw.domain

import com.graphite.competitionplanner.competitioncategory.domain.CloseForRegistrations
import com.graphite.competitionplanner.competitioncategory.interfaces.CompetitionCategoryDTO
import com.graphite.competitionplanner.draw.interfaces.ApproveSeedingSpec
import com.graphite.competitionplanner.draw.interfaces.ICompetitionDrawRepository
import com.graphite.competitionplanner.registration.repository.RegistrationRepository
import org.springframework.stereotype.Component

@Component
class ApproveSeeding(
    val drawRepository: ICompetitionDrawRepository,
    val registrationRepository: RegistrationRepository,
    val closeForRegistrations: CloseForRegistrations
) {

    /**
     * Approve and commit the given seeding. When a seeding is approved, the competition category will be closed
     * for further registrations as that would affect the seeding.
     */
    fun execute(competitionCategory: CompetitionCategoryDTO, spec: ApproveSeedingSpec) {
        if (spec.seeding.any { it.competitionCategoryId != competitionCategory.id }) {
            throw IllegalArgumentException("You are trying to submit a seeding that does not belong to the given competition category")
        }

        val registrationRankings = registrationRepository.getRegistrationRanking(competitionCategory)

        val actualRegistrationsIds = registrationRankings.map { it.registration.id }.sorted()
        val specRegistrationsIds = spec.seeding.map { it.registration.id }.sorted()

        if (actualRegistrationsIds != specRegistrationsIds) {
            throw IllegalArgumentException("You are trying to submit a seeding that is not including every registration")
        }

        val drawPolicy = DrawPolicy.createDrawStrategy(competitionCategory)

        val expectedNumberOfSeeded = drawPolicy.calculateNumberOfSeeds(actualRegistrationsIds.size)
        val actualNumberOfSeeded = spec.seeding.filter { it.seed != null }.size
        if (expectedNumberOfSeeded != actualNumberOfSeeded) {
            throw IllegalArgumentException("We expected $expectedNumberOfSeeded number of seeded registrations, but got $actualNumberOfSeeded")
        }

        drawPolicy.throwExceptionIfNotEnoughRegistrations(spec.seeding)

        drawRepository.asTransaction {
            drawRepository.storeSeeding(spec.seeding)
            closeForRegistrations.execute(competitionCategory)
        }
    }
}