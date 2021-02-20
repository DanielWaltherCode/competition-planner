package com.graphite.competitionplanner.security;

import com.graphite.competitionplanner.SpringApplicationContext;
import com.graphite.competitionplanner.util.Util;

public class SecurityConstants {
    public static final long EXPIRATION_TIME = 864000000;
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final String SIGN_UP_URL = "/user";

    public static String getTokenSecret() {
        Util utils = (Util) SpringApplicationContext.getBean("util");
        return utils.getTokenSecret();
    }
}
