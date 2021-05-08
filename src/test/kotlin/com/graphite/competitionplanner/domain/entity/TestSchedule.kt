package com.graphite.competitionplanner.domain.entity

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import java.lang.IllegalArgumentException

@SpringBootTest
class TestSchedule {

    @Test
    fun cannotHaveLessThanOneTable() {
        Assertions.assertThrows(IllegalArgumentException::class.java) { Schedule(0, 0, emptyList()) }
    }
}