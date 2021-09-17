package com.graphite.competitionplanner.registration.domain

import com.graphite.competitionplanner.category.interfaces.CategoryType
import com.graphite.competitionplanner.competitioncategory.domain.FindCompetitionCategory
import com.graphite.competitionplanner.player.domain.FindPlayer
import com.graphite.competitionplanner.registration.interfaces.IRegistrationRepository
import com.graphite.competitionplanner.registration.interfaces.RegistrationDoublesDTO
import com.graphite.competitionplanner.registration.interfaces.RegistrationDoublesSpec
import com.graphite.competitionplanner.registration.interfaces.RegistrationDoublesSpecWithDate
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
        findPlayer.byId(spec.playerOneId)
        findPlayer.byId(spec.playerTwoId)

        val competitionCategory = findCompetitionCategory.byId(spec.competitionCategoryId)
        if (competitionCategory.category.type != CategoryType.DOUBLES.name) {
            throw IllegalArgumentException("The given competition category id ${spec.competitionCategoryId} does not " +
                    "correspond to a category of type ${CategoryType.DOUBLES} ")
        }

        val playerIds = repository.getAllPlayerIdsRegisteredTo(spec.competitionCategoryId)
        if (playerIds.contains(spec.playerOneId) || playerIds.contains(spec.playerTwoId)) {
            return repository.getRegistrationFor(spec)
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