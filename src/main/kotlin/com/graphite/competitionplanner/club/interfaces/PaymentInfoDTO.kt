package com.graphite.competitionplanner.club.interfaces

data class PaymentInfoDTO(
    val id: Int,
    val club: ClubDTO,
    val recipient: String,
    val street: String,
    val postcode: String,
    val city: String,
    val plusgiro: String,
    val bankgiro: String,
    val bankAccountNr: String
)