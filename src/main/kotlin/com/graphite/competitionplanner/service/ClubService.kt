package com.graphite.competitionplanner.service

import com.graphite.competitionplanner.domain.usecase.club.*
import com.graphite.competitionplanner.domain.dto.ClubDTO
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
        return findClub.byName(clubName)
    }

    fun findById(clubId: Int): ClubDTO {
        return findClub.byId(clubId)
    }

    fun delete(clubId: Int): Boolean {
        deleteClub.execute(ClubDTO(clubId, "", ""))
        return true
    }

    fun getAll(): List<ClubDTO> {
        return listAllClubs.execute()
    }

    fun updateClub(clubDTO: ClubDTO): ClubDTO {
        return updateClub.execute(clubDTO)
    }

    fun addClub(clubDTO: ClubDTO): ClubDTO {
        return createClub.execute(clubDTO)
    }
}