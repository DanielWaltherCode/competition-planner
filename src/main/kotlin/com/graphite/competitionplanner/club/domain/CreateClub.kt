package com.graphite.competitionplanner.club.domain

import com.graphite.competitionplanner.club.interfaces.ClubDTO
import com.graphite.competitionplanner.club.interfaces.ClubSpec
import com.graphite.competitionplanner.club.interfaces.IClubRepository
import com.graphite.competitionplanner.club.interfaces.PaymentInfoSpec
import com.graphite.competitionplanner.club.repository.ClubPaymentRepository
import com.graphite.competitionplanner.common.exception.BadRequestException
import com.graphite.competitionplanner.common.exception.BadRequestType
import org.springframework.dao.DuplicateKeyException
import org.springframework.stereotype.Component

@Component
class CreateClub(
    val clubRepository: IClubRepository,
    val clubPaymentRepository: ClubPaymentRepository
) {

    fun execute(spec: ClubSpec): ClubDTO {
        lateinit var club: ClubDTO
        clubRepository.asTransaction {
            try {
                club = clubRepository.store(spec)
                clubPaymentRepository.add(club.id, getEmptyPaymentInfo())
            } catch (ex: DuplicateKeyException) {
                throw BadRequestException(
                    BadRequestType.CLUB_NAME_NOT_UNIQUE,
                    "There is already a club registered with this name: ${spec.name}")
            }
        }
        return club
    }

    fun executeForCompetition(competitionId: Int, spec: ClubSpec): ClubDTO {
        lateinit var club: ClubDTO
        clubRepository.asTransaction {
            try {
                club = clubRepository.storeForCompetition(competitionId, spec)
                clubPaymentRepository.add(club.id, getEmptyPaymentInfo())
            } catch (ex: DuplicateKeyException) {
                throw BadRequestException(
                    BadRequestType.CLUB_NAME_NOT_UNIQUE,
                    "There is already a club registered to this competition with this name: ${spec.name}")
            }
        }
        return club
    }

    private fun getEmptyPaymentInfo(): PaymentInfoSpec {
        return PaymentInfoSpec(
            "", "", "", "", "", "", "",
        )
    }
}