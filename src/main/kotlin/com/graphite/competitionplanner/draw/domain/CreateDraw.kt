package com.graphite.competitionplanner.draw.domain

import com.graphite.competitionplanner.competitioncategory.domain.FindCompetitionCategory
import com.graphite.competitionplanner.competitioncategory.interfaces.*
import com.graphite.competitionplanner.draw.interfaces.Round
import com.graphite.competitionplanner.draw.interfaces.*
import com.graphite.competitionplanner.registration.domain.Registration
import com.graphite.competitionplanner.registration.interfaces.IRegistrationRepository
import com.graphite.competitionplanner.registration.interfaces.RegistrationRankingDTO
import org.springframework.stereotype.Component

@Component
class CreateDraw(
    val findCompetitionCategory: FindCompetitionCategory,
    val repository: IRegistrationRepository,
    val drawRepository: ICompetitionDrawRepository,
    val competitionCategoryRepository: ICompetitionCategoryRepository
) {

    /**
     * Creates a draw for the given competition category
     *
     * @throws BadRequestException
     */
    fun execute(competitionCategoryId: Int): CompetitionCategoryDrawDTO {
        val competitionCategory: CompetitionCategoryDTO = findCompetitionCategory.byId(competitionCategoryId)

        if (competitionCategory.status == CompetitionCategoryStatus.DRAWN.name) {
            return drawRepository.get(competitionCategoryId)
        }

        val registrationRankings: List<RegistrationRankingDTO> = repository.getRegistrationRanking(competitionCategory)

        val drawPolicy = DrawPolicy.createDrawStrategy(competitionCategory)
        val registrationsWithSeeds = drawPolicy.createSeed(registrationRankings)
        drawPolicy.throwExceptionIfNotEnoughRegistrations(registrationsWithSeeds)

        val spec = drawPolicy.createDraw(registrationsWithSeeds)
        spec.seeding = registrationsWithSeeds

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
    var round: Round
)

data class PoolMatch(
    val registrationOneId: Registration.Real,
    val registrationTwoId: Registration.Real
)

