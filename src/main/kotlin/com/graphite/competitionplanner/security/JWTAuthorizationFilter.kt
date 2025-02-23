package com.graphite.competitionplanner.security

import com.graphite.competitionplanner.SpringApplicationContext
import com.graphite.competitionplanner.user.service.UserService
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException
import java.util.ArrayList
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JWTAuthorizationFilter : OncePerRequestFilter() {
    var entryPoint = JwtAuthenticationEntryPoint()

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            val header: String? = request.getHeader(SecurityConstants.HEADER_STRING)
            // Warning, as long as a bearer token is sent in, an attempt will made to validate it, even if the endpoint
            // doesn't actually require authentication. Solution, don't send bearer token for those endpoints.
            if (header == null || !header.startsWith(SecurityConstants.TOKEN_PREFIX)) {
                filterChain.doFilter(request, response)
                return
            }
            val authToken: UsernamePasswordAuthenticationToken? = getAuthentication(request)
            SecurityContextHolder.getContext().authentication = authToken
            filterChain.doFilter(request, response)
        } catch (exception: AuthenticationException) {
            entryPoint.commence(request, response, exception)
            return
        }
    }

    private fun getAuthentication(req: HttpServletRequest): UsernamePasswordAuthenticationToken? {
        var token: String? = req.getHeader(SecurityConstants.HEADER_STRING)
        if (token != null) {
            token = token.replace(SecurityConstants.TOKEN_PREFIX, "")
            if (SecurityHelper.validateToken(token)) {
                val username = SecurityHelper.getEmailFromToken(token)
                val userService: UserService = SpringApplicationContext.getBean("userService") as UserService
                val user = userService.getUserByEmail(username)
                val grantedAuthorities: List<GrantedAuthority> = if (user.role == null) {
                    emptyList()
                } else {
                    listOf(SimpleGrantedAuthority(user.role))
                }
                return UsernamePasswordAuthenticationToken(user, token, grantedAuthorities)
            }
            return null
        }
        return null
    }
}

