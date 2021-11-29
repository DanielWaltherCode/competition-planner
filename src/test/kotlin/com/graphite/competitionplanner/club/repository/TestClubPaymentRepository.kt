package com.graphite.competitionplanner.club.repository

import com.graphite.competitionplanner.club.domain.CreateClub
import com.graphite.competitionplanner.util.DataGenerator
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestClubPaymentRepository(
    @Autowired val paymentRepository: ClubPaymentRepository,
    @Autowired val createClub: CreateClub,
) {

    val dataGenerator = DataGenerator()
    @Test
    fun shouldGetDefaultPaymentInfo() {
        // Setup
        val spec = dataGenerator.newClubSpec("ClubY", "Address1")
        val originalPaymentInfoCount = paymentRepository.getCount()

        // Act
        createClub.execute(spec)

        // Creating a new club should mean that a new paymentInfo is also added
        val newCount = paymentRepository.getCount()

        // Assert
        Assertions.assertTrue(originalPaymentInfoCount + 1 == newCount)
    }
}