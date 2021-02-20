package com.graphite.competitionplanner.api

import com.graphite.competitionplanner.service.UserDTO
import com.graphite.competitionplanner.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.User
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
        val authentication = SecurityContextHolder.getContext().authentication
            ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED)

        val user = authentication.principal as String
        return userService.getUserByUsername(user)
    }
}

data class UserSpec(
    val username: String,
    val password: String,
    val clubId: Int
)
