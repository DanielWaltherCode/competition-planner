package com.graphite.competitionplanner.api

import com.graphite.competitionplanner.repositories.ClubRepository
import com.graphite.competitionplanner.tables.pojos.Club
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.server.ResponseStatusException
import javax.validation.Valid
import javax.validation.constraints.NotNull
import kotlin.streams.toList


@RestController
@RequestMapping("/club")
class ClubApi(val clubRepository: ClubRepository) {

    @PostMapping
    fun addClub(@Valid @RequestBody clubDTO: ClubDTO): ClubDTO {
        val club: Club = clubRepository.addClub(clubDTO)
        return ClubDTO(club.id, club.name, club.address)
    }

    @PutMapping
    fun updateClub(@Valid @RequestBody clubDTO: ClubDTO): ClubDTO {
        val club: Club = clubRepository.updateClub(clubDTO)
        return ClubDTO(club.id, club.name, club.address)
    }

    @GetMapping("/{clubName}")
    fun findByName(@PathVariable clubName: String): ClubDTO {
        val club = clubRepository.findByName(clubName)
        if (club == null) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND)
        }
        return ClubDTO(club.id, club.name, club.address)
    }

    @GetMapping
    fun getAll(): List<ClubDTO> {
        val clubs = clubRepository.getAll()
        return clubs.stream().map { c -> ClubDTO(c.id, c.name, c.address) }.toList()
    }
}

data class ClubDTO(
        val id: Int? = null,
        @NotNull
        val name: String,
        @NotNull
        val address: String
)