package com.graphite.competitionplanner.club.domain

import com.graphite.competitionplanner.club.interfaces.ClubDTO
import com.graphite.competitionplanner.club.interfaces.ClubSpec
import com.graphite.competitionplanner.club.interfaces.IClubRepository
import com.graphite.competitionplanner.club.interfaces.PaymentInfoSpec
import com.graphite.competitionplanner.club.repository.ClubPaymentRepository
import org.springframework.stereotype.Component

@Component
class CreateClub(
    val clubRepository: IClubRepository,
    val clubPaymentRepository: ClubPaymentRepository
) {

    fun execute(spec: ClubSpec): ClubDTO {
        val nameIsAvailable: Boolean = clubRepository.getAll().none { it.name == spec.name }
        if (nameIsAvailable) {
            val club: ClubDTO = clubRepository.store(spec)
            // Set up empty paymentinfo for each club
            clubPaymentRepository.add(club.id, getEmptyPaymentInfo())
            return club
        } else {
            throw IllegalArgumentException("Cannot add club. Club with name ${spec.name} already exist ")
        }
    }

    fun executeForCompetition(competitionId: Int, spec: ClubSpec): ClubDTO {
        val nameIsAvailable: Boolean = clubRepository.getAllClubsForCompetition(competitionId).none { it.name == spec.name }
        if (nameIsAvailable) {
            val club: ClubDTO = clubRepository.storeForCompetition(competitionId, spec)
            // Set up empty paymentinfo for each club
            clubPaymentRepository.add(club.id, getEmptyPaymentInfo())
            return club
        } else {
            throw IllegalArgumentException("Cannot add club. Club with name ${spec.name} already exist ")
        }
    }

    fun getEmptyPaymentInfo(): PaymentInfoSpec {
        return PaymentInfoSpec(
            "", "", "", "", "", "", "",
        )
    }
}