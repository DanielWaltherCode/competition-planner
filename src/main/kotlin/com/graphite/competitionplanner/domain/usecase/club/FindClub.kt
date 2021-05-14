package com.graphite.competitionplanner.domain.usecase.club

import com.graphite.competitionplanner.domain.dto.ClubDTO
import com.graphite.competitionplanner.domain.interfaces.IClubRepository
import org.springframework.stereotype.Component

@Component
class FindClub(
    val clubRepository: IClubRepository
) {

    fun byName(name: String): ClubDTO {
        return clubRepository.findClubByName(name)
    }

    fun byId(id: Int): ClubDTO {
        return clubRepository.findClubById(id)
    }
}