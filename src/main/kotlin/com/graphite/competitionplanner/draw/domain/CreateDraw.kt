package com.graphite.competitionplanner.draw.domain

import com.graphite.competitionplanner.competitioncategory.domain.FindCompetitionCategory
import com.graphite.competitionplanner.competitioncategory.interfaces.*
import com.graphite.competitionplanner.draw.interfaces.Round
import com.graphite.competitionplanner.draw.interfaces.*
import com.graphite.competitionplanner.registration.domain.Registration
import com.graphite.competitionplanner.registration.interfaces.IRegistrationRepository
import org.springframework.stereotype.Component

@Component
class CreateDraw(
    val findCompetitionCategory: FindCompetitionCategory,
    val repository: IRegistrationRepository,
    val drawRepository: ICompetitionDrawRepository,
    val competitionCategoryRepository: ICompetitionCategoryRepository,
    val getCurrentSeeding: GetCurrentSeeding,
    val approveSeeding: ApproveSeeding
) {

    /**
     * Creates a draw for the given competition category
     *
     */
    fun execute(competitionCategoryId: Int): CompetitionCategoryDrawDTO {
        val competitionCategory: CompetitionCategoryDTO = findCompetitionCategory.byId(competitionCategoryId)

        if (competitionCategory.status == CompetitionCategoryStatus.DRAWN) {
            return drawRepository.get(competitionCategoryId)
        }

        val registrationsWithSeeds = getCurrentSeeding.execute(competitionCategory)
        if (competitionCategory.status != CompetitionCategoryStatus.CLOSED_FOR_REGISTRATION) {
            approveSeeding.execute(competitionCategory, ApproveSeedingSpec(registrationsWithSeeds))
        }

        val drawPolicy = DrawPolicy.createDrawStrategy(competitionCategory)
        val spec = drawPolicy.createDraw(registrationsWithSeeds)

        return drawRepository.store(spec)
    }
}

sealed class CompetitionCategoryDrawSpec(
    val competitionCategoryId: Int,
    var seeding: List<RegistrationSeedDTO> = emptyList()
)

class CupDrawSpec(
    competitionCategoryId: Int,
    val matches: List<PlayOffMatch>
) : CompetitionCategoryDrawSpec(competitionCategoryId)

class PoolDrawSpec(
    competitionCategoryId: Int,
    val pools: List<Pool>
) : CompetitionCategoryDrawSpec(competitionCategoryId)

class PoolAndCupDrawSpec(
    competitionCategoryId: Int,
    val pools: List<Pool>,
    val matches: List<PlayOffMatch> = emptyList(),
) : CompetitionCategoryDrawSpec(competitionCategoryId)

data class Pool(
    val name: String,
    val registrationIds: List<Registration.Real>,
    var matches: List<PoolMatch>
)

data class PlayOffMatch(
    var registrationOneId: Registration,
    var registrationTwoId: Registration,
    var order: Int,
    var round: Round,
    /**
     * Winner of the match. This happens when a real player goes up against a BYE in first round in play off.
     */
    var winner: Registration? = null
)

data class PoolMatch(
    val registrationOneId: Registration.Real,
    val registrationTwoId: Registration.Real
)

