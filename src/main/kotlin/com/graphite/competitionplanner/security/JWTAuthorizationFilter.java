package com.graphite.competitionplanner.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

public class JWTAuthorizationFilter extends OncePerRequestFilter {

    JwtAuthenticationEntryPoint entryPoint = new JwtAuthenticationEntryPoint();


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String header = request.getHeader(SecurityConstants.HEADER_STRING);
            // Warning, as long as a bearer token is sent in, an attempt will made to validate it, even if the endpoint
            // doesn't actually require authentication. Solution, don't send bearer token for those endpoints.
            if (header == null || !header.startsWith(SecurityConstants.TOKEN_PREFIX) ) {
                filterChain.doFilter(request, response);
                return;
            }

            UsernamePasswordAuthenticationToken authToken = getAuthentication(request);
            SecurityContextHolder.getContext().setAuthentication(authToken);
            filterChain.doFilter(request, response);

        } catch (AuthenticationException exception) {
            entryPoint.commence(request, response, exception);
            return;
        }
        filterChain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest req) {
        String token = req.getHeader(SecurityConstants.HEADER_STRING);

        if (token != null) {
            token = token.replace(SecurityConstants.TOKEN_PREFIX, "");

            if(SecurityHelper.validateToken(token)) {
                String user = SecurityHelper.getUsernameFromToken(token);
                return new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());

            }
            return null;
        }
        return null;
    }
}
