package com.graphite.competitionplanner.domain.entity

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import java.lang.IllegalArgumentException

@SpringBootTest
class TestScheduleMetaData {

    @Test
    fun averageMatchTimeCannotBeLessThanOne() {
        Assertions.assertThrows(IllegalArgumentException::class.java) { ScheduleMetaData(0, 15) }
    }

    @Test
    fun numberOfTablesCannotBeLessThanOne() {
        Assertions.assertThrows(IllegalArgumentException::class.java) { ScheduleMetaData(10, 0) }
    }

}