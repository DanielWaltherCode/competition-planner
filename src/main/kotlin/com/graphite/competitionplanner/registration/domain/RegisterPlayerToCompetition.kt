package com.graphite.competitionplanner.registration.domain

import com.graphite.competitionplanner.competitioncategory.domain.FindCompetitionCategory
import com.graphite.competitionplanner.player.domain.FindPlayer
import com.graphite.competitionplanner.registration.interfaces.IRegistrationRepository
import com.graphite.competitionplanner.registration.interfaces.RegistrationSinglesDTO
import com.graphite.competitionplanner.registration.interfaces.RegistrationSinglesSpec
import com.graphite.competitionplanner.registration.interfaces.RegistrationSinglesSpecWithDate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class RegisterPlayerToCompetition(
    @Autowired val findPlayer: FindPlayer,
    @Autowired val findCompetitionCategory: FindCompetitionCategory,
    @Autowired val repository: IRegistrationRepository
) {

    fun execute(spec: RegistrationSinglesSpec): RegistrationSinglesDTO {
        // TODO: We need to prevent the same player being added twice to the same competition
        findPlayer.byId(spec.playerId)
        findCompetitionCategory.byId(spec.competitionCategoryId)

        return repository.store(
            RegistrationSinglesSpecWithDate(
                LocalDate.now(),
                spec.playerId,
                spec.competitionCategoryId
            )
        )
    }
}