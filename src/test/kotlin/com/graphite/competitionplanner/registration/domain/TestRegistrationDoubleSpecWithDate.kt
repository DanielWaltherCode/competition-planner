package com.graphite.competitionplanner.registration.domain

import com.graphite.competitionplanner.registration.interfaces.RegistrationDoublesSpecWithDate
import com.graphite.competitionplanner.util.DataGenerator
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDate

@SpringBootTest
class TestRegistrationDoubleSpecWithDate {


    @Test
    fun playerOneAndTwoCannotHaveSameId() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            RegistrationDoublesSpecWithDate(LocalDate.now(), 1, 1, 3)
        }
    }

    @Test
    fun shouldNotThrowException() {
        Assertions.assertDoesNotThrow {
            DataGenerator().newRegistrationDoublesSpecWithDate()
        }
    }
}