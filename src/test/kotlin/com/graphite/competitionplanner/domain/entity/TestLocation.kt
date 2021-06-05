package com.graphite.competitionplanner.domain.entity

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import java.lang.IllegalArgumentException

@SpringBootTest
class TestLocation {

    @Test
    fun nameCannotBeEmpty() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            Location("")
        }
    }

    @Test
    fun nameCannotBeBlank() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            Location(" ")
        }
    }
}