package com.graphite.competitionplanner.util

import com.graphite.competitionplanner.repositories.ClubRepository
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component

@Component
class Util(val clubRepository: ClubRepository,
val env: Environment
) {

    fun getClubIdOrDefault(clubName: String): Int {
        return clubRepository.findByName(clubName)?.id ?: 0
    }

    fun getTokenSecret(): String {
        return env.getProperty("tokenSecret")!!
    }

}

