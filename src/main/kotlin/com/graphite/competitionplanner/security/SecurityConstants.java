package com.graphite.competitionplanner.security;

import com.graphite.competitionplanner.SpringApplicationContext;
import com.graphite.competitionplanner.util.Util;

public class SecurityConstants {
    // Access token valid 1 day
    public static final long ACCESS_TOKEN_EXPIRATION_TIME = 86400; // Is multiplied by 1000 when used
    public static final long REFRESH_TOKEN_EXPIRATION_TIME = 8640000;
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final String SIGN_UP_URL = "/user";

    public static String getTokenSecret() {
        Util utils = (Util) SpringApplicationContext.getBean("util");
        return utils.getTokenSecret();
    }
}
