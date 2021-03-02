package com.graphite.competitionplanner.api

import com.graphite.competitionplanner.repositories.ClubRepository
import com.graphite.competitionplanner.service.ClubService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import javax.validation.Valid
import kotlin.streams.toList


@RestController
@RequestMapping("/club")
class ClubApi(val clubRepository: ClubRepository,
val clubService: ClubService) {

    @PostMapping
    fun addClub(@Valid @RequestBody clubDTO: ClubDTO): ClubDTO {
        val club = clubRepository.addClub(clubDTO)
        return ClubDTO(club.id, club.name, club.address)
    }

    @PutMapping
    fun updateClub(@Valid @RequestBody clubDTO: ClubDTO): ClubDTO {
        val club = clubRepository.updateClub(clubDTO)
        return ClubDTO(club.id, club.name, club.address)
    }

    @GetMapping("/{clubName}")
    fun findByName(@PathVariable clubName: String): ClubDTO {
        return clubService.findByName(clubName)
    }

    @GetMapping("/{clubId}")
    fun findById(@PathVariable clubId: Int): ClubDTO {
        return clubService.findById(clubId)
    }

    @GetMapping
    fun getAll(): List<ClubDTO> {
        val clubs = clubRepository.getAll()
        return clubs.stream().map { c -> ClubDTO(c.id, c.name, c.address) }.toList()
    }

    @DeleteMapping("/{clubId}")
    fun deleteClub(@PathVariable clubId: Int): Boolean {
        return clubRepository.deleteClub(clubId)
    }
}

data class ClubDTO(
        val id: Int? = null,
        val name: String,
        val address: String
)

data class ClubNoAddressDTO(
        val id: Int,
        val name: String?
)