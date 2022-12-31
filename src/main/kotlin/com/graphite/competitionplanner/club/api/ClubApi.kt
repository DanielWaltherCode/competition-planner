package com.graphite.competitionplanner.club.api

import com.graphite.competitionplanner.club.domain.*
import com.graphite.competitionplanner.club.interfaces.ClubDTO
import com.graphite.competitionplanner.club.interfaces.ClubSpec
import com.graphite.competitionplanner.club.interfaces.PaymentInfoDTO
import com.graphite.competitionplanner.club.interfaces.PaymentInfoSpec
import com.graphite.competitionplanner.club.repository.ClubPaymentRepository
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/club")
class ClubApi(
    val paymentRepository: ClubPaymentRepository,
    val createClub: CreateClub,
    val listAllClubs: ListAllClubs,
    val findClub: FindClub
) {

    @PostMapping("/competition/{competitionId}")
    fun addClubForCompetition(@PathVariable competitionId: Int, @RequestBody clubSpec: ClubSpec): ClubDTO {
        return createClub.executeForCompetition(competitionId, clubSpec)
    }

    @GetMapping("/{clubId}")
    fun findById(@PathVariable clubId: Int): ClubDTO {
        return findClub.byId(clubId)
    }

    @GetMapping
    fun getAll(): List<ClubDTO> {
        return listAllClubs.execute()
    }

    @GetMapping("competition/{competitionId}")
    fun getAllClubsForCompetition(@PathVariable competitionId: Int): List<ClubDTO> {
        return listAllClubs.executeForCompetition(competitionId)
    }

    @GetMapping("/{clubId}/payment-info/")
    fun getPaymentInfo(@PathVariable clubId: Int): PaymentInfoDTO {
        return paymentRepository.get(clubId)
    }

    @PutMapping("/{clubId}/payment-info/{paymentInfoId}")
    fun updatePaymentInfo(
        @PathVariable clubId: Int,
        @PathVariable paymentInfoId: Int,
        @RequestBody paymentInfoSpec: PaymentInfoSpec
    ): PaymentInfoDTO {
        return paymentRepository.update(paymentInfoId, clubId, paymentInfoSpec)
    }
}
