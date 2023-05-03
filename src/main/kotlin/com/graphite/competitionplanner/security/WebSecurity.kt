package com.graphite.competitionplanner.security

import com.graphite.competitionplanner.user.service.UserService
import org.springframework.boot.autoconfigure.security.servlet.PathRequest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import java.lang.Exception
import java.util.*

@Configuration
@EnableWebSecurity
class WebSecurity(
        private val userDetailsService: UserService
) : WebSecurityConfigurerAdapter() {
    var jwtAuthorizationFilter = JWTAuthorizationFilter()
    var jwtAuthEntryPoint = JwtAuthenticationEntryPoint()

    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        http.csrf().disable()
                .exceptionHandling().authenticationEntryPoint(jwtAuthEntryPoint)
                .and().authorizeRequests().antMatchers("/api/util/**").permitAll()
                .and().authorizeRequests().antMatchers("/api/request-token/**").permitAll()
                .and().authorizeRequests().antMatchers("/api/**").authenticated()
                .and().authorizeRequests().antMatchers("/api/admin/**").hasRole("ADMIN")
                .and().authorizeRequests().antMatchers("/**").permitAll()
                .and().cors() // by default uses a Bean by the type CorsConfigurationSource (defined below)
                .and()
                .addFilter(AuthenticationFilter(authenticationManager()))
                .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter::class.java)
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.allowCredentials = true
        configuration.allowedOrigins = listOf("http://localhost:8080",
                "http://localhost:3000",
                "http://localhost:8081",
                "http://167.71.65.197",
                "http://competition.travexperten.nu",
                "https://competition.travexperten.nu",
                "http://www.competition.travexperten.nu",
                "https://www.competition.travexperten.nu",
                "http://open.competition.travexperten.nu",
                "https://open.competition.travexperten.nu",
                "http://www.open.competition.travexperten.nu",
                "https://www.open.competition.travexperten.nu")
        configuration.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS")
        configuration.allowedHeaders = listOf(
                "X-Requested-With",
                "Origin",
                "Content-Type",
                "Accept",
                "Authorization",
                "Access-Control-Allow-Credentials"
        )
        // This allows us to expose the headers
        configuration.exposedHeaders =
                Arrays.asList(
                        "Access-Control-Allow-Headers",
                        "Access-Control-Allow-Credentials",
                        "Authorization, x-xsrf-token, Access-Control-Allow-Headers, Origin, Accept, X-Requested-With, " +
                                "Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers"
                )
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }

    @Throws(Exception::class)
    public override fun configure(auth: AuthenticationManagerBuilder) {
        auth.userDetailsService(userDetailsService)
    }
}
