package com.graphite.competitionplanner.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.graphite.competitionplanner.SpringApplicationContext;
import com.graphite.competitionplanner.api.LoginDTO;
import com.graphite.competitionplanner.service.UserService;
import com.graphite.competitionplanner.util.UserLogin;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;

    public AuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res) throws AuthenticationException {
        try {
            UserLogin loginCredentials = new ObjectMapper().readValue(req.getInputStream(), UserLogin.class);

            // Spring automatically connects to the database, validates the password, and returns the user object
            // This works because we implented the loadUserByUsername method in UserService
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    loginCredentials.getUsername(), loginCredentials.getPassword(), new ArrayList<>()
            );

            return authenticationManager.authenticate(authenticationToken);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest req,
                                            HttpServletResponse res,
                                            FilterChain chain,
                                            Authentication auth) throws IOException, ServletException {
        String username = ((User) auth.getPrincipal()).getUsername();
        String accessToken = SecurityHelper.generateAccessToken(username);
        String refreshToken = SecurityHelper.generateRefreshToken(username);

        // Store refresh token with user
        UserService userService = (UserService) SpringApplicationContext.getBean("userService");
        userService.storeRefreshToken(refreshToken, username);

        // Add access and refresh tokens to response body
        PrintWriter out = res.getWriter();
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        LoginDTO loginDTO = new LoginDTO(accessToken, refreshToken);
        String jsonBody = new ObjectMapper().writeValueAsString(loginDTO);
        out.print(jsonBody);
        out.flush();
    }
}
