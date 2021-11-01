package com.graphite.competitionplanner.user.service

import com.graphite.competitionplanner.club.interfaces.ClubNoAddressDTO
import com.graphite.competitionplanner.club.service.ClubService
import com.graphite.competitionplanner.security.SecurityHelper
import com.graphite.competitionplanner.tables.records.UserTableRecord
import com.graphite.competitionplanner.user.api.LoginDTO
import com.graphite.competitionplanner.user.api.UserSpec
import com.graphite.competitionplanner.user.repository.UserRepository
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

    // Will either update existing or add new one
    fun storeRefreshToken(refreshToken: String, username: String) {
        val user = getUserByUsername(username)
        val existingRecord = userRepository.getRefreshTokenByUser(user.id)
        if (existingRecord == null) {
            userRepository.saveRefreshToken(refreshToken, user.id)
        }
        else {
           userRepository.updateRefreshToken(existingRecord.id, refreshToken, existingRecord.userId)
        }
    }

    fun getNewAccessToken(refreshToken: String): LoginDTO {
        val tokenRecord = userRepository.getRefreshToken(refreshToken)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "No such refresh token found")

        if (SecurityHelper.validateToken(tokenRecord.refreshToken)) {
            val user = userRepository.getUserById(tokenRecord.userId)
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "No user matching the record found")
            val accessToken = SecurityHelper.generateAccessToken(user.username)
            val newRefreshToken = SecurityHelper.generateRefreshToken(user.username)
            storeRefreshToken(newRefreshToken, user.username)
            return LoginDTO(accessToken, newRefreshToken)
        }
        else {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Refresh token not valid")
        }
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
            ClubNoAddressDTO(club.id, club.name)
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