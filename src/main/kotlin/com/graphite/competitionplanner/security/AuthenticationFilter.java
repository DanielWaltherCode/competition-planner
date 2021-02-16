package com.graphite.competitionplanner.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.graphite.competitionplanner.api.Login;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;

    public AuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res) throws AuthenticationException {
        try {
            Login loginCredentials = new ObjectMapper().readValue(req.getInputStream(), Login.class);

            return authenticationManager.authenticate(
                    // Spring automatically connects to the database, validates the password, and returns the user object
                    // This works because we implented the loadUserByUsername method in UserServiceImpl
                    new UsernamePasswordAuthenticationToken(
                            loginCredentials.getUserName(), loginCredentials.getPassword(), new ArrayList<>()
                    )
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
