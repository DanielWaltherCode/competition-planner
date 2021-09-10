package com.graphite.competitionplanner.club.domain

import com.graphite.competitionplanner.club.domain.interfaces.ClubDTO
import com.graphite.competitionplanner.club.domain.interfaces.IClubRepository
import com.graphite.competitionplanner.common.exception.NotFoundException
import org.springframework.stereotype.Component

@Component
class FindClub(
    val clubRepository: IClubRepository
) {
    /**
     * Returns Club with the given name
     *
     * @throws NotFoundException If club cannot be found
     */
    fun byName(name: String): ClubDTO {
        return clubRepository.findByName(name)
    }

    /**
     * Returns Club with the given Id
     *
     * @throws NotFoundException If club cannot be found
     */
    @Throws(NotFoundException::class)
    fun byId(id: Int): ClubDTO {
        return clubRepository.findById(id)
    }
}