package com.graphite.competitionplanner.util

import com.graphite.competitionplanner.repositories.ClubRepository
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import kotlin.time.Duration

@Component
class Util(val clubRepository: ClubRepository,
val env: Environment
) {

    fun getClubIdOrDefault(clubName: String): Int {
        return clubRepository.findByName(clubName).id
    }

    fun getTokenSecret(): String {
        return env.getProperty("tokenSecret")!!
    }

}

/**
 * Adds the duration to the LocalDateTime and return a copy of it
 */
fun LocalDateTime.plusDuration(duration: Duration): LocalDateTime {
    return this.plusMinutes(duration.inMinutes.toLong())
}
