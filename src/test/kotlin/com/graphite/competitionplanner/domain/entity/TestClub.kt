package com.graphite.competitionplanner.domain.entity

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import java.lang.IllegalArgumentException

@SpringBootTest
class TestClub {

    @Test
    fun shouldThrowIllegalExceptionWhenNameIsEmpty() {
        Assertions.assertThrows(IllegalArgumentException::class.java) { Club(0, "", "Kirunavägen 13") }
    }

    @Test
    fun shouldThrowIllegalExceptionWhenAddressIsEmpty() {
        Assertions.assertThrows(IllegalArgumentException::class.java) { Club(0, "Kiruna IK", "") }
    }

}