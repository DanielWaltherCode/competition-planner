package com.graphite.competitionplanner.user.api

import com.graphite.competitionplanner.user.service.UserDTO
import com.graphite.competitionplanner.user.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException


@RestController
@RequestMapping("/user")
class UserApi(val userService: UserService) {

    @PostMapping
    fun register(@RequestBody userSpec: UserSpec): UserDTO {
        return userService.addUser(userSpec)
    }

    @GetMapping
    fun getLoggedInUser(): UserDTO {
        return userService.getLoggedInUser()
    }

}

@RestController
@RequestMapping("/request-token")
class RefreshTokenApi(val userService: UserService) {

    @GetMapping("/{refreshToken}")
    fun getNewAccessToken(@PathVariable refreshToken: String): LoginDTO {
        return userService.getNewAccessToken(refreshToken)
    }
}

data class UserSpec(
    val username: String,
    val password: String,
    val clubId: Int
)

class LoginDTO(
    val accessToken: String,
    val refreshToken: String
)