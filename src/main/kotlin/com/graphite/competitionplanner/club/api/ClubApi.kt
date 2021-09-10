package com.graphite.competitionplanner.club.api

import com.graphite.competitionplanner.domain.dto.ClubDTO
import com.graphite.competitionplanner.club.service.ClubService
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/club")
class ClubApi(
    val clubService: ClubService
) {

    @PostMapping
    fun addClub(@Valid @RequestBody clubSpec: NewClubSpec): ClubSpec {
        val dto = clubService.addClub(ClubDTO(0, clubSpec.name, clubSpec.address))
        return ClubSpec(dto)
    }

    @PutMapping
    fun updateClub(@Valid @RequestBody clubSpec: ClubSpec): ClubSpec {
        val dtoUpdate = ClubDTO(clubSpec.id, clubSpec.name, clubSpec.address)
        val updatedDto = clubService.updateClub(dtoUpdate)
        return ClubSpec(updatedDto)
    }

    // TODO: These two GetMappings (clubname, and clubId) are ambiguous. Server does not know how to route requests
//    @GetMapping("/{clubName}")
//    fun findByName(@PathVariable clubName: String): ClubSpec {
//        val dto = clubService.findByName(clubName)
//        return ClubSpec(dto)
//    }

    @GetMapping("/{clubId}")
    fun findById(@PathVariable clubId: Int): ClubSpec {
        val dto = clubService.findById(clubId)
        return ClubSpec(dto)
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

data class NewClubSpec(
    val name: String,
    val address: String
)

data class ClubSpec(
    val id: Int,
    val name: String,
    val address: String
) {
    constructor(dto: ClubDTO) : this(dto.id, dto.name, dto.address)
}

data class ClubNoAddressDTO(
    val id: Int,
    val name: String?
)