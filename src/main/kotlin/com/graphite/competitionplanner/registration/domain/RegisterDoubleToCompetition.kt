package com.graphite.competitionplanner.registration.domain

import com.graphite.competitionplanner.category.interfaces.CategoryType
import com.graphite.competitionplanner.competitioncategory.domain.FindCompetitionCategory
import com.graphite.competitionplanner.competitioncategory.interfaces.CompetitionCategoryDTO
import com.graphite.competitionplanner.player.domain.FindPlayer
import com.graphite.competitionplanner.player.interfaces.PlayerWithClubDTO
import com.graphite.competitionplanner.registration.interfaces.IRegistrationRepository
import com.graphite.competitionplanner.registration.interfaces.RegistrationDoublesDTO
import com.graphite.competitionplanner.registration.interfaces.RegistrationDoublesSpec
import com.graphite.competitionplanner.registration.interfaces.RegistrationDoublesSpecWithDate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.server.ResponseStatusException
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
        if (competitionCategory.category.type != CategoryType.DOUBLES.name) {
            throw IllegalArgumentException("The given competition category id ${spec.competitionCategoryId} does not " +
                    "correspond to a category of type ${CategoryType.DOUBLES} ")
        }

        val playerIds: List<Int> = repository.getAllPlayerIdsRegisteredTo(spec.competitionCategoryId)
        if (playerIds.contains(spec.playerOneId)) {
            // Todo - gör om till nytt exception med en egen felkod som webben kan mappa till ett felmeddelande
            throw ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Player ${playerOne.firstName + " " + playerOne.lastName} is already registered in this category")
        }
        if (playerIds.contains(spec.playerTwoId)) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Player ${playerTwo.firstName + " " + playerTwo.lastName} is already registered in this category")
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