package com.graphite.competitionplanner.registration.domain

import com.graphite.competitionplanner.category.interfaces.CategoryType
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
        if (competitionCategory.category.type != CategoryType.SINGLES.name) {
            throw IllegalArgumentException("The given competition category id ${spec.competitionCategoryId} does not " +
                    "correspond to a category of type ${CategoryType.SINGLES} ")
        }

        val playerIds: List<Int> = registrationRepository.getAllPlayerIdsRegisteredTo(spec.competitionCategoryId)
        if (playerIds.contains(spec.playerId)) {
            // If player is already registered, check if he is withdrawn. If so, set status to active again
            val playerRegistrationId =
                registrationRepository.getRegistrationIdForPlayerInCategory(spec.competitionCategoryId, spec.playerId)
            val registrationRecord = registrationRepository.getPlayerRegistration(playerRegistrationId)
            if (registrationRecord.status == PlayerRegistrationStatus.WITHDRAWN.name) {
                registrationRepository.updatePlayerRegistrationStatus(playerRegistrationId,
                    PlayerRegistrationStatus.PLAYING.name)
            }
            else {
                throw PlayerAlreadyRegisteredException(player)
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