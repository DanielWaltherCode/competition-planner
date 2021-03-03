package com.graphite.competitionplanner.security

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.MediaType
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import java.io.IOException
import java.io.PrintWriter
import java.util.*
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JwtAuthenticationEntryPoint : AuthenticationEntryPoint {

    @Throws(IOException::class, ServletException::class)
    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        exception: AuthenticationException
    ) {
        // TODO: Distinguish 401 from 403 (i.e. listen to actual exception)
        response.status = HttpServletResponse.SC_UNAUTHORIZED
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        val message: String?
        message = if (exception.cause != null) {
            exception.cause!!.message
        } else {
            exception.message
        }
        val body = ObjectMapper()
            .writeValueAsString(Collections.singletonMap("error", message))
        val out = response.writer
        out.print(body)
    }
}


