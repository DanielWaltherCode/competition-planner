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
    val deleteClub: DeleteClub,
    val createClub: CreateClub,
    val updateClub: UpdateClub,
    val listAllClubs: ListAllClubs,
    val findClub: FindClub
) {

    @PostMapping
    fun addClub(@RequestBody clubSpec: ClubSpec): ClubDTO {
        return createClub.execute(clubSpec)
    }

    @PutMapping("/{clubId}")
    fun updateClub(@PathVariable clubId: Int, @RequestBody clubSpec: ClubSpec): ClubDTO {
        return updateClub.execute(clubId, clubSpec)
    }

    @GetMapping("/{clubId}")
    fun findById(@PathVariable clubId: Int): ClubDTO {
        return findClub.byId(clubId)
    }

    @GetMapping
    fun getAll(): List<ClubDTO> {
        return listAllClubs.execute()
    }

    @DeleteMapping("/{clubId}")
    fun deleteClub(@PathVariable clubId: Int): Boolean {
        return deleteClub.execute(clubId)
    }

    @GetMapping("/{clubId}/payment-info/")
    fun getPaymentInfo(@PathVariable clubId: Int): PaymentInfoDTO {
        return paymentRepository.get(clubId)
    }

    @PutMapping("/{clubId}/payment-info/{paymentInfoId}")
    fun updatePaymentInfo(@PathVariable clubId: Int, @PathVariable paymentInfoId: Int, @RequestBody paymentInfoSpec: PaymentInfoSpec): PaymentInfoDTO {
        return paymentRepository.update(paymentInfoId, clubId, paymentInfoSpec)
    }
}
