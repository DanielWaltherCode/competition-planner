package com.graphite.competitionplanner.user.service

import com.graphite.competitionplanner.club.domain.FindClub
import com.graphite.competitionplanner.club.interfaces.ClubNoAddressDTO
import com.graphite.competitionplanner.security.SecurityHelper
import com.graphite.competitionplanner.tables.records.UserTableRecord
import com.graphite.competitionplanner.user.api.LoginDTO
import com.graphite.competitionplanner.user.api.UserSpec
import com.graphite.competitionplanner.user.repository.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.security.core.context.SecurityContextHolder
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
    val findClub: FindClub
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

    fun getUserByEmail(email: String): UserDTO {
        val userRecord = userRepository.getUserByEmail(email)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "No user with that username found")

        return recordToDTO(userRecord)
    }

    fun getUserById(id: Int): UserDTO {
        val userRecord = userRepository.getUserById(id)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "No user with that username found")

        return recordToDTO(userRecord)
    }

    fun getLoggedInUser(): UserDTO {
        val authentication = SecurityContextHolder.getContext().authentication
            ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED)

        val username = authentication.principal as String
        return getUserByEmail(username)
    }

    // Will either update existing or add new one
    fun storeRefreshToken(refreshToken: String, username: String) {
        val user = getUserByEmail(username)
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
            val user: UserDTO = getUserById(tokenRecord.userId)
            val accessToken = SecurityHelper.generateAccessToken(user)
            val newRefreshToken = SecurityHelper.generateRefreshToken(user)
            storeRefreshToken(newRefreshToken, user.email)
            return LoginDTO(accessToken, newRefreshToken)
        }
        else {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Refresh token not valid")
        }
    }

    /*Override necessary method from UserDetailsService */
    override fun loadUserByUsername(username: String): UserDetails {
        val userRecord = userRepository.getUserByEmail(username)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "No user with that username found")

        return User(userRecord.email, userRecord.password, mutableListOf())
    }

    fun recordToDTO(userRecord: UserTableRecord): UserDTO {
        val club = findClub.byId(userRecord.clubid)
        return UserDTO(
            userRecord.id,
            userRecord.email,
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
    val email: String,
    val clubNoAddressDTO: ClubNoAddressDTO
)