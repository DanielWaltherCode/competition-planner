package com.graphite.competitionplanner.security;

import io.jsonwebtoken.*;
import org.springframework.security.authentication.BadCredentialsException;

import java.util.Date;

public class SecurityHelper {
    public static boolean validateToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(SecurityConstants.getTokenSecret()).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException ex) {
            throw new BadCredentialsException("INVALID_CREDENTIALS", ex);
        } catch (ExpiredJwtException ex) {
            throw new JWTAuthenticationException("Access token expired");
        }
    }

    public static String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser().setSigningKey(SecurityConstants.getTokenSecret()).parseClaimsJws(token).getBody();
        return claims.getSubject();
    }

    public static String generateAccessToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setExpiration(new Date(System.currentTimeMillis() + (SecurityConstants.ACCESS_TOKEN_EXPIRATION_TIME)))
                .signWith(SignatureAlgorithm.HS256, SecurityConstants.getTokenSecret())
                .compact();
    }

    public static String generateRefreshToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setExpiration(new Date(System.currentTimeMillis() + (SecurityConstants.REFRESH_TOKEN_EXPIRATION_TIME * 1000)))
                .signWith(SignatureAlgorithm.HS256, SecurityConstants.getTokenSecret())
                .compact();
    }
}
