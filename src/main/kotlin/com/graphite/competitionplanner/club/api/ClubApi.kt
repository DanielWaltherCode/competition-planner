package com.graphite.competitionplanner.club.api

import com.graphite.competitionplanner.club.domain.*
import com.graphite.competitionplanner.club.interfaces.*
import com.graphite.competitionplanner.club.repository.ClubPaymentRepository
import com.graphite.competitionplanner.user.service.UserDTO
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("api/club")
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

    @GetMapping("/logged-in-club")
    fun getLoggedInClubId(authentication: Authentication): ClubNoAddressDTO {
        val principal = authentication.principal as UserDTO
        return principal.clubNoAddressDTO
    }
}
