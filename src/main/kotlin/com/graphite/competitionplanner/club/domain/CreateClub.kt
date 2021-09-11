package com.graphite.competitionplanner.club.domain

import com.graphite.competitionplanner.club.interfaces.ClubDTO
import com.graphite.competitionplanner.club.interfaces.ClubSpec
import com.graphite.competitionplanner.club.interfaces.IClubRepository
import org.springframework.stereotype.Component

@Component
class CreateClub(
    val clubRepository: IClubRepository
) {

    fun execute(spec: ClubSpec): ClubDTO {
        val nameIsAvailable = clubRepository.getAll().none { it.name == spec.name }
        if (nameIsAvailable) {
            return clubRepository.store(spec)
        } else {
            throw IllegalArgumentException("Cannot add club. Club with name ${spec.name} already exist ")
        }
    }
}