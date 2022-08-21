package com.graphite.competitionplanner.registration.domain

import com.graphite.competitionplanner.category.domain.CategoryType
import com.graphite.competitionplanner.competitioncategory.domain.FindCompetitionCategory
import com.graphite.competitionplanner.competitioncategory.interfaces.CompetitionCategoryDTO
import com.graphite.competitionplanner.player.domain.FindPlayer
import com.graphite.competitionplanner.player.interfaces.PlayerWithClubDTO
import com.graphite.competitionplanner.registration.interfaces.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class RegisterDoubleToCompetition(
    @Autowired val findPlayer: FindPlayer,
    @Autowired val findCompetitionCategory: FindCompetitionCategory,
    @Autowired val repository: IRegistrationRepository
) {

    fun execute(spec: RegistrationDoublesSpec): RegistrationDoublesDTO {
        val playerOne: PlayerWithClubDTO = findPlayer.byId(spec.playerOneId)
        val playerTwo: PlayerWithClubDTO = findPlayer.byId(spec.playerTwoId)

        val competitionCategory: CompetitionCategoryDTO = findCompetitionCategory.byId(spec.competitionCategoryId)
        if (competitionCategory.category.type != CategoryType.DOUBLES) {
            throw IllegalArgumentException("The given competition category id ${spec.competitionCategoryId} does not " +
                    "correspond to a category of type ${CategoryType.DOUBLES}. ${competitionCategory} ")
        }

        val playerIds: List<Int> = repository.getAllPlayerIdsRegisteredTo(spec.competitionCategoryId)
        if (playerIds.contains(spec.playerOneId)) {
            throw PlayerAlreadyRegisteredException(playerOne)
        }
        if (playerIds.contains(spec.playerTwoId)) {
            throw PlayerAlreadyRegisteredException(playerTwo)
        }

        return repository.storeDoubles(
            RegistrationDoublesSpecWithDate(
                LocalDate.now(),
                spec.playerOneId,
                spec.playerTwoId,
                spec.competitionCategoryId
            )
        )
    }
}