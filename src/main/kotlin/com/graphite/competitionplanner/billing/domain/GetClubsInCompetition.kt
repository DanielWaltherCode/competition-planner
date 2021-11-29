package com.graphite.competitionplanner.billing.domain

import com.graphite.competitionplanner.club.interfaces.ClubDTO
import com.graphite.competitionplanner.club.interfaces.IClubRepository
import org.springframework.stereotype.Component

@Component
class GetClubsInCompetition(val clubRepository: IClubRepository) {

    fun execute(competitionId: Int): List<ClubDTO> {
        return clubRepository.getClubsInCompetition(competitionId)
    }
}