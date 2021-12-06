package com.graphite.competitionplanner.domain.entity

import com.graphite.competitionplanner.util.DataGenerator
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDateTime
import kotlin.time.Duration
import kotlin.time.minutes

@SpringBootTest
class TestScheduleSettings {

    val dataGenerator = DataGenerator()

    @Test
    fun averageMatchTimeCannotBeLessThanOne() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            dataGenerator.newScheduleSettings(
                averageMatchTime = Duration.minutes(0)
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
                averageMatchTime = Duration.minutes(15),
                startTime = startTime,
                endTime = startTime.plusMinutes(15).minusSeconds(1)
            )
        }
    }

    @Test
    fun shouldNotThrowIfDifferenceBetweenStartAndEndTimeIsGreaterOrEqualToTheAverageMatchTime() {
        val startTime = LocalDateTime.now()
        Assertions.assertDoesNotThrow {
            dataGenerator.newScheduleSettings(
                averageMatchTime = Duration.minutes(15),
                startTime = startTime,
                endTime = startTime.plusMinutes(15)
            )
        }
    }
}