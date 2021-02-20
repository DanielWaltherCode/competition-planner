package com.graphite.competitionplanner.service

import com.graphite.competitionplanner.api.ClubNoAddressDTO
import com.graphite.competitionplanner.api.UserSpec
import com.graphite.competitionplanner.repositories.ClubRepository
import com.graphite.competitionplanner.repositories.UserRepository
import com.graphite.competitionplanner.tables.records.UserTableRecord
import org.springframework.http.HttpStatus
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class UserService(
    val userRepository: UserRepository,
    val bCryptPasswordEncoder: BCryptPasswordEncoder,
    val clubService: ClubService
) : UserDetailsService {

    fun addUser(userSpec: UserSpec): UserDTO {
        //TODO check if username is free, check if club id is correct
        val user = UserWithEncryptedPassword(
            userSpec.username,
            bCryptPasswordEncoder.encode(userSpec.password),
            userSpec.clubId
        )
        val addedUser = userRepository.addUser(user)
        return recordToDTO(addedUser)
    }

    fun getUserByUsername(username: String): UserDTO {
        val userRecord = userRepository.getUserByUsername(username)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "No user with that username found")

        return recordToDTO(userRecord)
    }

    /*Override necessary method from UserDetailsService */
    override fun loadUserByUsername(username: String): UserDetails {
        val userRecord = userRepository.getUserByUsername(username)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "No user with that username found")

        return User(userRecord.username, userRecord.password, mutableListOf())
    }

    fun recordToDTO(userRecord: UserTableRecord): UserDTO {
        val club = clubService.findById(userRecord.clubid)
        return UserDTO(
            userRecord.id,
            userRecord.username,
            ClubNoAddressDTO(club.id!!, club.name)
        )
    }
}



data class UserWithEncryptedPassword(
    val username: String,
    val encryptedPassword: String,
    val clubId: Int
)

data class UserDTO(
    val id: Int,
    val username: String,
    val clubNoAddressDTO: ClubNoAddressDTO
)