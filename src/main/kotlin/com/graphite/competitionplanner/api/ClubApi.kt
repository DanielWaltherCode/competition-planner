package com.graphite.competitionplanner.api

import com.graphite.competitionplanner.repositories.ClubRepository
import com.graphite.competitionplanner.service.ClubService
import org.springframework.web.bind.annotation.*
import javax.validation.Valid


@RestController
@RequestMapping("/club")
class ClubApi(val clubRepository: ClubRepository,
val clubService: ClubService) {

    @PostMapping
    fun addClub(@Valid @RequestBody clubDTO: ClubDTO): ClubDTO {
        return clubService.addClub(clubDTO)
    }

    @PutMapping
    fun updateClub(@Valid @RequestBody clubDTO: ClubDTO): ClubDTO {
        return clubService.updateClub(clubDTO)
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
        return clubService.getAll()
    }

    @DeleteMapping("/{clubId}")
    fun deleteClub(@PathVariable clubId: Int): Boolean {
        return clubService.delete(clubId)
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