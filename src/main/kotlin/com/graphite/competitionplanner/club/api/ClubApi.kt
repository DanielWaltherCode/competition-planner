package com.graphite.competitionplanner.club.api

import com.graphite.competitionplanner.club.interfaces.ClubDTO
import com.graphite.competitionplanner.club.interfaces.ClubSpec
import com.graphite.competitionplanner.club.service.ClubService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/club")
class ClubApi(
    val clubService: ClubService
) {

    @PostMapping
    fun addClub(@RequestBody clubSpec: ClubSpec): ClubDTO {
        return clubService.addClub(clubSpec)
    }

    @PutMapping("/{clubId}")
    fun updateClub(@PathVariable clubId: Int, @RequestBody clubSpec: ClubSpec): ClubDTO {
        return clubService.updateClub(clubId, clubSpec)
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
