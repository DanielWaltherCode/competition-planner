package com.graphite.competitionplanner.util

import com.graphite.competitionplanner.repositories.ClubRepository
import com.graphite.competitionplanner.security.SecurityConstants
import io.jsonwebtoken.*
import org.springframework.core.env.Environment
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.stereotype.Component
import org.springframework.web.server.ResponseStatusException

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

