package com.graphite.competitionplanner.club.domain

import com.graphite.competitionplanner.club.interfaces.ClubDTO
import com.graphite.competitionplanner.club.interfaces.IClubRepository
import org.springframework.stereotype.Component

@Component
class ListAllClubs(
    val clubRepository: IClubRepository
) {

    fun execute(): List<ClubDTO> {
        return clubRepository.getAll()
    }
}