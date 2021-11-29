package com.graphite.competitionplanner.club.repository

import com.graphite.competitionplanner.Tables.PAYMENT_INFO
import com.graphite.competitionplanner.club.domain.FindClub
import com.graphite.competitionplanner.club.interfaces.PaymentInfoDTO
import com.graphite.competitionplanner.club.interfaces.PaymentInfoSpec
import com.graphite.competitionplanner.common.exception.NotFoundException
import com.graphite.competitionplanner.tables.records.PaymentInfoRecord
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import org.springframework.web.client.HttpServerErrorException

@Repository
class ClubPaymentRepository(
    val dslContext: DSLContext,
    val findClub: FindClub
) {
    fun add(clubId: Int, paymentInfoSpec: PaymentInfoSpec): PaymentInfoDTO {
        val paymentInfoRecord = dslContext.newRecord(PAYMENT_INFO)
        paymentInfoRecord.clubId = clubId
        paymentInfoRecord.recipient = paymentInfoSpec.recipient
        paymentInfoRecord.street = paymentInfoSpec.street
        paymentInfoRecord.postcode = paymentInfoSpec.postcode
        paymentInfoRecord.city = paymentInfoSpec.city
        paymentInfoRecord.plusgiro = paymentInfoSpec.plusgiro
        paymentInfoRecord.bankgiro = paymentInfoSpec.bankgiro
        paymentInfoRecord.bankAccountNr = paymentInfoSpec.bankAccountNr
        paymentInfoRecord.store()

        return recordToDto(paymentInfoRecord)
    }

    fun update(paymentInfoId: Int, clubId: Int, paymentInfoSpec: PaymentInfoSpec): PaymentInfoDTO {
        val paymentInfoRecord = dslContext.newRecord(PAYMENT_INFO)
        paymentInfoRecord.id = paymentInfoId
        paymentInfoRecord.clubId = clubId
        paymentInfoRecord.recipient = paymentInfoSpec.recipient
        paymentInfoRecord.street = paymentInfoSpec.street
        paymentInfoRecord.postcode = paymentInfoSpec.postcode
        paymentInfoRecord.city = paymentInfoSpec.city
        paymentInfoRecord.plusgiro = paymentInfoSpec.plusgiro
        paymentInfoRecord.bankgiro = paymentInfoSpec.bankgiro
        paymentInfoRecord.bankAccountNr = paymentInfoSpec.bankAccountNr
        val rowsUpdated = paymentInfoRecord.update()
        if (rowsUpdated < 1) {
            throw NotFoundException("Could not update. Club with id $clubId not found.")
        }
        return recordToDto(paymentInfoRecord)
    }

    fun get(clubId: Int): PaymentInfoDTO {
        val record = dslContext.selectFrom(PAYMENT_INFO)
            .where(PAYMENT_INFO.CLUB_ID.eq(clubId))
            .fetchOneInto(PAYMENT_INFO) ?: throw NotFoundException("Could not get paymentinfo. Club with id $clubId not found.")

        return recordToDto(record)
    }

    fun getCount(): Int {
        return dslContext.selectCount().from(PAYMENT_INFO).fetchOne(0, Int::class.java) ?: throw Exception("Couldn't retrieve count")
    }

    fun recordToDto(paymentInfoRecord: PaymentInfoRecord): PaymentInfoDTO {
        return PaymentInfoDTO(
            paymentInfoRecord.id,
            findClub.byId(paymentInfoRecord.clubId),
            paymentInfoRecord.recipient,
            paymentInfoRecord.street,
            paymentInfoRecord.postcode,
            paymentInfoRecord.city,
            paymentInfoRecord.plusgiro,
            paymentInfoRecord.bankgiro,
            paymentInfoRecord.bankAccountNr
        )
    }
}