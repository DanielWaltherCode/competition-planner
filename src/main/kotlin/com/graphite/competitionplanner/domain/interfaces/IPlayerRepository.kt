package com.graphite.competitionplanner.domain.interfaces

import com.graphite.competitionplanner.domain.dto.ClubDTO
import com.graphite.competitionplanner.domain.dto.PlayerDTO

interface IPlayerRepository {

    /**
     * Saves the Player
     *
     * @param dto Player to be stored
     */
    fun store(dto: PlayerDTO): PlayerDTO

    /**
     * Return all players for the given club
     */
    fun playersInClub(dto: ClubDTO): List<PlayerDTO>

}