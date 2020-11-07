package com.graphite.competitionplanner.api

import com.graphite.competitionplanner.repositories.ClubRepository
import com.graphite.competitionplanner.tables.pojos.Club
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid
import javax.validation.constraints.NotNull


@RestController
@RequestMapping("/club")
class ClubApi(val clubRepository: ClubRepository) {

    @PostMapping
    fun addClub(@Valid @RequestBody clubDTO: ClubDTO): ClubDTO {
        val club: Club = clubRepository.addClub(clubDTO)
        return ClubDTO(club.id, club.name, club.address)
    }
}

data class ClubDTO(
        val id: Int? = null,
        @NotNull
        val name: String,
        @NotNull
        val address: String
)