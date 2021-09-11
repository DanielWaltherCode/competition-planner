package com.graphite.competitionplanner.club.domain

import com.graphite.competitionplanner.club.interfaces.IClubRepository
import com.graphite.competitionplanner.player.domain.interfaces.IPlayerRepository
import org.springframework.stereotype.Component

@Component
class DeleteClub(
    val clubRepository: IClubRepository,
    val playerRepository: IPlayerRepository
) {

    /**
     * Deletes the club
     *
     * @param dto Club to delete
     * @return Club that was deleted
     */
    fun execute(clubId: Int): Boolean {
        if (playerRepository.playersInClub(clubId)
                .isNotEmpty()
        ) throw Exception("Cannot delete a club that has players.")
        return clubRepository.delete(clubId)
    }

}