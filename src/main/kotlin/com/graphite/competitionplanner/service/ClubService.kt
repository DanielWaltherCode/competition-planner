package com.graphite.competitionplanner.service

import com.graphite.competitionplanner.api.ClubDTO
import com.graphite.competitionplanner.repositories.ClubRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.server.ResponseStatusException

@Service
class ClubService(val clubRepository: ClubRepository) {

    fun findByName(clubName: String): ClubDTO {
        val club = clubRepository.findByName(clubName) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
        return ClubDTO(club.id, club.name, club.address)
    }

    fun findById(clubId: Int): ClubDTO {
        val club = clubRepository.getById(clubId) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
        return ClubDTO(club.id, club.name, club.address)
    }
}