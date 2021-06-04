package com.graphite.competitionplanner.domain.entity

import com.graphite.competitionplanner.DataGenerator
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import java.lang.IllegalArgumentException
import java.time.LocalDateTime
import kotlin.time.minutes

@SpringBootTest
class TestScheduleSettings {

    val dataGenerator = DataGenerator()

    @Test
    fun averageMatchTimeCannotBeLessThanOne() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            dataGenerator.newScheduleSettings(
                averageMatchTime = 0.minutes
            )
        }
    }

    @Test
    fun numberOfTablesCannotBeLessThanOne() {
        Assertions.assertThrows(IllegalArgumentException::class.java) { dataGenerator.newScheduleSettings(numberOfTables = 0) }
    }

    @Test
    fun shouldThrowIfDifferenceBetweenStartAndEndTimeIsLessTheAverageMatchTime() {
        val startTime = LocalDateTime.now()
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            dataGenerator.newScheduleSettings(
                averageMatchTime = 15.minutes,
                startTime = startTime,
                endTime = startTime.plusMinutes(15).minusSeconds(1)
            )
        }
    }

    @Test
    fun shouldNotThrowIfDifferenceBetweenStartAndEndTimeIsGreaterOrEqualToTheAverageMatchTime() {
        val startTime = LocalDateTime.now()
        Assertions.assertDoesNotThrow() {
            dataGenerator.newScheduleSettings(
                averageMatchTime = 15.minutes,
                startTime = startTime,
                endTime = startTime.plusMinutes(15)
            )
        }
    }
}