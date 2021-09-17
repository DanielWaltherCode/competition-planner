package com.graphite.competitionplanner.registration.domain

import com.graphite.competitionplanner.registration.interfaces.RegistrationDoublesSpec
import com.graphite.competitionplanner.util.DataGenerator
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestRegistrationDoubleSpec {

    @Test
    fun playerOneAndTwoCannotHaveSameId() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            RegistrationDoublesSpec(1, 1, 3)
        }
    }

    @Test
    fun shouldNotThrowException() {
        Assertions.assertDoesNotThrow {
            DataGenerator().newRegistrationDoublesSpec()
        }
    }
}