package com.graphite.competitionplanner.registration.domain

import com.graphite.competitionplanner.category.domain.CategoryType
import com.graphite.competitionplanner.common.exception.BadRequestException
import com.graphite.competitionplanner.common.exception.BadRequestType
import com.graphite.competitionplanner.competitioncategory.domain.FindCompetitionCategory
import com.graphite.competitionplanner.competitioncategory.interfaces.CompetitionCategoryDTO
import com.graphite.competitionplanner.player.domain.FindPlayer
import com.graphite.competitionplanner.player.interfaces.PlayerWithClubDTO
import com.graphite.competitionplanner.registration.interfaces.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class RegisterPlayerToCompetition(
    @Autowired val findPlayer: FindPlayer,
    @Autowired val findCompetitionCategory: FindCompetitionCategory,
    @Autowired val registrationRepository: IRegistrationRepository
) {

    fun execute(spec: RegistrationSinglesSpec): RegistrationSinglesDTO {
        val player: PlayerWithClubDTO = findPlayer.byId(spec.playerId)

        val competitionCategory: CompetitionCategoryDTO = findCompetitionCategory.byId(spec.competitionCategoryId)
        if (competitionCategory.category.type != CategoryType.SINGLES) {
            throw BadRequestException(BadRequestType.REGISTRATION_WRONG_CATEGORY_TYPE, "The given competition category id ${spec.competitionCategoryId} does not " +
                    "correspond to a category of type ${CategoryType.SINGLES} ")
        }

        val registrations: List<RegistrationSinglesDTO> = registrationRepository.getAllSingleRegistrations(spec.competitionCategoryId)
        val registration = registrations.find { it.playerId == spec.playerId }
        if (registration != null) {
            // If player is already registered, check if he is withdrawn. If so, set status to active again
            if (registration.status == PlayerRegistrationStatus.WITHDRAWN) {
                registrationRepository.updatePlayerRegistrationStatus(registration.id, PlayerRegistrationStatus.PLAYING)
                return registrationRepository.getRegistrationFor(spec)
            }
            else {
                throw throw BadRequestException(BadRequestType.PLAYER_ALREADY_REGISTERED,
                        "Player ${player.firstName} ${player.lastName} is already registered in this category")
            }
        }

        return registrationRepository.storeSingles(
            RegistrationSinglesSpecWithDate(
                LocalDate.now(),
                spec.playerId,
                spec.competitionCategoryId
            )
        )
    }
}