package com.graphite.competitionplanner.security

import com.graphite.competitionplanner.SpringApplicationContext
import com.graphite.competitionplanner.util.Util

object SecurityConstants {
    // Both values here are in seconds, and are multiplied by 1000 to get milliseconds when used in SecurityHelper
    const val ACCESS_TOKEN_EXPIRATION_TIME: Long = 1800 // 30 minutes
    const val REFRESH_TOKEN_EXPIRATION_TIME: Long = 2592000 // One month
    const val TOKEN_PREFIX = "Bearer "
    const val HEADER_STRING = "Authorization"
    const val SIGN_UP_URL = "/user"

    val tokenSecret: String
        get() {
            val utils = SpringApplicationContext.getBean("util") as Util
            return utils.getTokenSecret()
        }
}

