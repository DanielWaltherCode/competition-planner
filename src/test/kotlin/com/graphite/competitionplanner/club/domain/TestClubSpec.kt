package com.graphite.competitionplanner.club.domain

import com.graphite.competitionplanner.club.interfaces.ClubSpec
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestClubSpec {

    @Test
    fun shouldThrowIllegalExceptionWhenNameIsEmpty() {
        Assertions.assertThrows(IllegalArgumentException::class.java) { ClubSpec("", "Kirunavägen 13") }
    }

    @Test
    fun shouldThrowIllegalExceptionWhenAddressIsEmpty() {
        Assertions.assertThrows(IllegalArgumentException::class.java) { ClubSpec("Kiruna IK", "") }
    }

}