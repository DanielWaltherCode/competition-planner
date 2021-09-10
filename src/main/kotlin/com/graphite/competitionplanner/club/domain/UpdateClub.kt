package com.graphite.competitionplanner.club.domain

import com.graphite.competitionplanner.club.domain.interfaces.ClubDTO
import com.graphite.competitionplanner.domain.entity.Club
import com.graphite.competitionplanner.club.domain.interfaces.IClubRepository
import org.springframework.stereotype.Component

@Component
class UpdateClub(val clubRepository: IClubRepository) {

    fun execute(dto: ClubDTO): ClubDTO {
        Club(dto)

        val allOtherClubs = clubRepository.getAll().filter { it.id != dto.id }
        val nameIsAvailable = allOtherClubs.none { it.name == dto.name }

        if (nameIsAvailable) {
            return clubRepository.update(dto)
        } else {
            throw Exception("Cannot update club name to ${dto.name} since there is already a club with that name.")
        }
    }
}