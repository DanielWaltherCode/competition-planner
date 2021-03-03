package com.graphite.competitionplanner.security

import org.springframework.http.HttpStatus
import org.springframework.security.core.AuthenticationException
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.BAD_REQUEST)
class JWTAuthenticationException(message: String?) : AuthenticationException(message)
