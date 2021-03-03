package com.graphite.competitionplanner.security

import com.graphite.competitionplanner.SpringApplicationContext
import com.graphite.competitionplanner.util.Util

object SecurityConstants {
    // Access token valid 1 day
    const val ACCESS_TOKEN_EXPIRATION_TIME: Long = 86400 // Is multiplied by 1000 when used
    const val REFRESH_TOKEN_EXPIRATION_TIME: Long = 8640000
    const val TOKEN_PREFIX = "Bearer "
    const val HEADER_STRING = "Authorization"
    const val SIGN_UP_URL = "/user"

    val tokenSecret: String
        get() {
            val utils = SpringApplicationContext.getBean("util") as Util
            return utils.getTokenSecret()
        }
}

