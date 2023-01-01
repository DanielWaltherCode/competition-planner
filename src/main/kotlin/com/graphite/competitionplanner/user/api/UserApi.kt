package com.graphite.competitionplanner.user.api

import com.graphite.competitionplanner.user.service.UserDTO
import com.graphite.competitionplanner.user.service.UserService
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("api/user")
class UserApi(val userService: UserService) {

    @GetMapping
    fun getLoggedInUser(): UserDTO {
        return userService.getLoggedInUser()
    }

}

@RestController
@RequestMapping("api/request-token")
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