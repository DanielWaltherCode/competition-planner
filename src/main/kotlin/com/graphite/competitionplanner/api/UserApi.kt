package com.graphite.competitionplanner.api

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/user")
class UserApi {

}

data class UserSpec(
    val userName: String,
    val password: String,
    val clubId: Int
)

data class Login(
    val userName: String,
    val password: String
)