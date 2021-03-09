package com.graphite.competitionplanner.security

import io.jsonwebtoken.*
import org.springframework.security.authentication.BadCredentialsException
import java.util.*

object SecurityHelper {
    fun validateToken(authToken: String?): Boolean {
        return try {
            Jwts.parser().setSigningKey(SecurityConstants.tokenSecret).parseClaimsJws(authToken)
            true
        } catch (ex: SignatureException) {
            throw BadCredentialsException("INVALID_CREDENTIALS", ex)
        } catch (ex: MalformedJwtException) {
            throw BadCredentialsException("INVALID_CREDENTIALS", ex)
        } catch (ex: UnsupportedJwtException) {
            throw BadCredentialsException("INVALID_CREDENTIALS", ex)
        } catch (ex: IllegalArgumentException) {
            throw BadCredentialsException("INVALID_CREDENTIALS", ex)
        } catch (ex: ExpiredJwtException) {
            throw JWTAuthenticationException("Access token expired")
        }
    }

    fun getUsernameFromToken(token: String?): String {
        val claims: Claims =
            Jwts.parser().setSigningKey(SecurityConstants.tokenSecret).parseClaimsJws(token).getBody()
        return claims.subject
    }

    fun generateAccessToken(username: String?): String {
        return Jwts.builder()
            .setSubject(username)
            .setExpiration(Date(System.currentTimeMillis() + SecurityConstants.ACCESS_TOKEN_EXPIRATION_TIME * 1000))
            .signWith(SignatureAlgorithm.HS256, SecurityConstants.tokenSecret)
            .compact()
    }

    fun generateRefreshToken(username: String?): String {
        return Jwts.builder()
            .setSubject(username)
            .setExpiration(Date(System.currentTimeMillis() + SecurityConstants.REFRESH_TOKEN_EXPIRATION_TIME * 1000))
            .signWith(SignatureAlgorithm.HS256, SecurityConstants.tokenSecret)
            .compact()
    }
}

