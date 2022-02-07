package com.graphite.competitionplanner.registration.interfaces

import com.graphite.competitionplanner.match.service.MatchAndResultDTO
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
     * All matches of the player in this category
     */
    val matches: List<MatchAndResultDTO>,

    /**
     * The player the registered person is playing with in the category
     * Null if the registration is in a singles tournament
     */
    val accompanyingPlayer: PlayerWithClubDTO?,

    /**
     * Says whether the player is still playing or has withdrawn/given WO
     */
    val registrationStatus: PlayerRegistrationStatus
)