package com.graphite.competitionplanner.security

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.AuthenticationException
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
            val header = request.getHeader(SecurityConstants.HEADER_STRING)
            // Warning, as long as a bearer token is sent in, an attempt will made to validate it, even if the endpoint
            // doesn't actually require authentication. Solution, don't send bearer token for those endpoints.
            if (header == null || !header.startsWith(SecurityConstants.TOKEN_PREFIX)) {
                filterChain.doFilter(request, response)
                return
            }
            val authToken = getAuthentication(request)
            SecurityContextHolder.getContext().authentication = authToken
            filterChain.doFilter(request, response)
        } catch (exception: AuthenticationException) {
            entryPoint.commence(request, response, exception)
            return
        }
    }

    private fun getAuthentication(req: HttpServletRequest): UsernamePasswordAuthenticationToken? {
        var token = req.getHeader(SecurityConstants.HEADER_STRING)
        if (token != null) {
            token = token.replace(SecurityConstants.TOKEN_PREFIX, "")
            if (SecurityHelper.validateToken(token)) {
                val user = SecurityHelper.getUsernameFromToken(token)
                return UsernamePasswordAuthenticationToken(user, null, ArrayList())
            }
            return null
        }
        return null
    }
}

