package com.graphite.competitionplanner.club.domain

import com.graphite.competitionplanner.club.interfaces.ClubDTO
import com.graphite.competitionplanner.club.interfaces.ClubSpec
import com.graphite.competitionplanner.club.interfaces.IClubRepository
import org.springframework.stereotype.Component

@Component
class UpdateClub(val clubRepository: IClubRepository) {

    fun execute(clubId: Int, spec: ClubSpec): ClubDTO {
        val allOtherClubs: List<ClubDTO> = clubRepository.getAll().filter { it.id != clubId }
        val nameIsAvailable: Boolean = allOtherClubs.none { it.name == spec.name }

        if (nameIsAvailable) {
            return clubRepository.update(clubId, spec)
        } else {
            throw Exception("Cannot update club name to ${spec.name} since there is already a club with that name.")
        }
    }

}