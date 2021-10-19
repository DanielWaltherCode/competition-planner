package com.graphite.competitionplanner.registration.interfaces

import com.graphite.competitionplanner.player.interfaces.PlayerWithClubDTO
import com.graphite.competitionplanner.registration.service.CompetitionCategoryWithTypeDTO

data class CategoryRegistrationDTO(
    /**
     * Registration id
     */
    val id: Int,
    /**
     * DTO with categoryID, name and type
     */
    val competitionCategory: CompetitionCategoryWithTypeDTO,

    /**
     * The player the registered person is playing with in the category
     * Null if the registration is in a singles tournament
     */
    val accompanyingPlayer: PlayerWithClubDTO?
)