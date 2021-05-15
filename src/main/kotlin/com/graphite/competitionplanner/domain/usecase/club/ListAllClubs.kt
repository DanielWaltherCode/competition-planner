package com.graphite.competitionplanner.domain.usecase.club

import com.graphite.competitionplanner.domain.dto.ClubDTO
import com.graphite.competitionplanner.domain.interfaces.IClubRepository
import org.springframework.stereotype.Component

@Component
class ListAllClubs(
    val clubRepository: IClubRepository
) {

    fun execute(): List<ClubDTO> {
        return clubRepository.getAll()
    }
}