package com.graphite.competitionplanner.registration.interfaces

import com.graphite.competitionplanner.player.interfaces.PlayerWithClubDTO

data class PlayerRegistrationDTO(
    val player: PlayerWithClubDTO,
    val registrations: List<CategoryRegistrationDTO>
)
