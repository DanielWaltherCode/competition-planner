package com.graphite.competitionplanner.competition.domain

import com.graphite.competitionplanner.competition.interfaces.LocationSpec
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestLocationSpec {

    @Test
    fun nameCannotBeEmpty() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            LocationSpec("")
        }
    }

    @Test
    fun nameCannotBeBlank() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            LocationSpec(" ")
        }
    }
}