package com.graphite.competitionplanner.domain.usecase.club

import com.graphite.competitionplanner.domain.dto.ClubDTO
import com.graphite.competitionplanner.domain.interfaces.IClubRepository
import com.graphite.competitionplanner.domain.interfaces.IPlayerRepository
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
    fun execute(dto: ClubDTO): ClubDTO {
        if (playerRepository.playersInClub(dto).isNotEmpty()) throw Exception("Cannot delete a club that has players.")
        return clubRepository.delete(dto)
    }

}