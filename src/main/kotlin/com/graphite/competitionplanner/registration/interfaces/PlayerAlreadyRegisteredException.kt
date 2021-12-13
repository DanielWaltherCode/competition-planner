package com.graphite.competitionplanner.registration.interfaces

import com.graphite.competitionplanner.player.interfaces.PlayerWithClubDTO

class PlayerAlreadyRegisteredException(player: PlayerWithClubDTO) :
    Exception("Player ${player.firstName} ${player.lastName} is already registered in this category")