package com.graphite.competitionplanner.domain.entity

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import java.lang.IllegalArgumentException
import java.time.LocalDateTime

@SpringBootTest
class TestScheduleSettings {

    @Test
    fun averageMatchTimeCannotBeLessThanOne() {
        Assertions.assertThrows(IllegalArgumentException::class.java) { ScheduleSettings(0, 15, LocalDateTime.now()) }
    }

    @Test
    fun numberOfTablesCannotBeLessThanOne() {
        Assertions.assertThrows(IllegalArgumentException::class.java) { ScheduleSettings(10, 0, LocalDateTime.now()) }
    }

}