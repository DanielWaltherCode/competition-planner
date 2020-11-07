package com.graphite.competitionplanner.util

import com.graphite.competitionplanner.repositories.ClubRepository
import org.springframework.stereotype.Component

@Component
class Util(val clubRepository: ClubRepository) {

    fun getClubIdOrDefault(clubName: String): Int {
        return clubRepository.findByName(clubName)?.id ?: 0
    }
}

