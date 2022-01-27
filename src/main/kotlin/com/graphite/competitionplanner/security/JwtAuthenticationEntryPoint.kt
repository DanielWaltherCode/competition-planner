package com.graphite.competitionplanner.security

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
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

    val logger = LoggerFactory.getLogger(JwtAuthenticationEntryPoint::class.java)

    @Throws(IOException::class, ServletException::class)
    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        exception: AuthenticationException
    ) {
        // TODO: Distinguish 401 from 403 (i.e. listen to actual exception)
        response.status = HttpServletResponse.SC_UNAUTHORIZED
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        val message: String? = if (exception.cause != null) {
            exception.cause!!.message
        } else {
            exception.message
        }
        val body: String = ObjectMapper()
            .writeValueAsString(Collections.singletonMap("error", message))
        val out: PrintWriter = response.writer
        out.print(body)
    }
}


