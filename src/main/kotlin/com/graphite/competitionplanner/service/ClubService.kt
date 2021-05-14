package com.graphite.competitionplanner.service

import com.graphite.competitionplanner.api.ClubDTO
import com.graphite.competitionplanner.domain.usecase.club.*
import org.springframework.stereotype.Service

@Service
class ClubService(
    val deleteClub: DeleteClub,
    val createClub: CreateClub,
    val updateClub: UpdateClub,
    val listAllClubs: ListAllClubs,
    val findClub: FindClub
) {

    fun findByName(clubName: String): ClubDTO {

        val club = findClub.byName(clubName)
        return ClubDTO(club.id, club.name, club.address)
    }

    fun findById(clubId: Int): ClubDTO {
        val club = findClub.byId(clubId)
        return ClubDTO(club.id, club.name, club.address)
    }

    fun delete(clubId: Int): Boolean {
        deleteClub.execute(com.graphite.competitionplanner.domain.dto.ClubDTO(clubId, "", ""))
        return true
    }

    fun getAll(): List<ClubDTO> {
        return listAllClubs.execute().map { c -> ClubDTO(c.id, c.name, c.address) }.toList()
    }

    fun updateClub(clubDTO: ClubDTO): ClubDTO {
        val toNewDto = com.graphite.competitionplanner.domain.dto.ClubDTO(clubDTO.id!!, clubDTO.name, clubDTO.address)
        val updated = updateClub.execute(toNewDto)
        return ClubDTO(updated.id, updated.name, updated.address)
    }

    fun addClub(clubDTO: ClubDTO): ClubDTO {
        val dto = com.graphite.competitionplanner.domain.dto.ClubDTO(0, clubDTO.name, clubDTO.address)
        val savedDto = createClub.execute(dto)
        return ClubDTO(savedDto.id, savedDto.name, savedDto.address)
    }
}