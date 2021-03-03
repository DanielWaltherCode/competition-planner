package com.graphite.competitionplanner.security

import com.fasterxml.jackson.databind.ObjectMapper
import com.graphite.competitionplanner.SpringApplicationContext
import com.graphite.competitionplanner.api.LoginDTO
import com.graphite.competitionplanner.service.UserService
import com.graphite.competitionplanner.util.UserLogin
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import java.io.IOException
import java.io.PrintWriter
import java.util.ArrayList
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class AuthenticationFilter(authenticationManager: AuthenticationManager) : UsernamePasswordAuthenticationFilter() {

    private val authManager: AuthenticationManager = authenticationManager

    @Throws(AuthenticationException::class)
    override fun attemptAuthentication(req: HttpServletRequest, res: HttpServletResponse): Authentication {
        return try {
            val loginCredentials = ObjectMapper().readValue(req.inputStream, UserLogin::class.java)

            // Spring automatically connects to the database, validates the password, and returns the user object
            // This works because we implented the loadUserByUsername method in UserService
            val authenticationToken = UsernamePasswordAuthenticationToken(
                loginCredentials.username, loginCredentials.password, ArrayList()
            )
            authManager.authenticate(authenticationToken)
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    @Throws(IOException::class, ServletException::class)
    override fun successfulAuthentication(
        req: HttpServletRequest,
        res: HttpServletResponse,
        chain: FilterChain,
        auth: Authentication
    ) {
        val username = (auth.principal as User).username
        val accessToken = SecurityHelper.generateAccessToken(username)
        val refreshToken = SecurityHelper.generateRefreshToken(username)

        // Store refresh token with user
        val userService = SpringApplicationContext.getBean("userService") as UserService
        userService.storeRefreshToken(refreshToken, username)

        // Add access and refresh tokens to response body
        val out = res.writer
        res.contentType = "application/json"
        res.characterEncoding = "UTF-8"
        val loginDTO = LoginDTO(accessToken, refreshToken)
        val jsonBody = ObjectMapper().writeValueAsString(loginDTO)
        out.print(jsonBody)
        out.flush()
    }
}

