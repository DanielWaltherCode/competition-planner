package com.graphite.competitionplanner.club.service

import com.graphite.competitionplanner.club.domain.*
import com.graphite.competitionplanner.club.interfaces.ClubDTO
import com.graphite.competitionplanner.club.interfaces.ClubSpec
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
        return deleteClub.execute(clubId)
    }

    fun getAll(): List<ClubDTO> {
        return listAllClubs.execute()
    }

    fun updateClub(clubId: Int, spec: ClubSpec): ClubDTO {
        return updateClub.execute(clubId, spec)
    }

    fun addClub(spec: ClubSpec): ClubDTO {
        return createClub.execute(spec)
    }
}