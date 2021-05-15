package com.graphite.competitionplanner.domain.usecase.club

import com.graphite.competitionplanner.domain.dto.ClubDTO
import com.graphite.competitionplanner.domain.entity.Club
import com.graphite.competitionplanner.domain.interfaces.IClubRepository
import org.springframework.stereotype.Component

@Component
class CreateClub(
    val clubRepository: IClubRepository
) {

    fun execute(dto: ClubDTO): ClubDTO {
        Club(dto) // Validating

        val nameIsAvailable = clubRepository.getAll().none { it.name == dto.name }
        if (nameIsAvailable) {
            return clubRepository.store(dto)
        }
        return dto;
    }
}