package com.graphite.competitionplanner.schedule.domain

import com.graphite.competitionplanner.schedule.interfaces.IScheduleRepository
import com.graphite.competitionplanner.util.DataGenerator
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDate
import kotlin.time.Duration


@SpringBootTest
class TestTrySchedule {

    private val mockedScheduleRepository: IScheduleRepository = Mockito.mock(IScheduleRepository::class.java)
    private val createSchedule = CreateSchedule()
    private val trySchedule = TrySchedule(mockedScheduleRepository, createSchedule)

    private val dataGenerator = DataGenerator()

    @Test
    fun whenAllMatchesFitInTimeInterval() {
        // Setup
        val competitionId = 1
        val spec = PreScheduleSpec(LocalDate.now(), TimeInterval.MORNING, 22)
        val settings = dataGenerator.newScheduleSettingsDTO(
            averageMatchTime = Duration.minutes(20),
            numberOfTables = 5,
        )
        `when`(mockedScheduleRepository.getMatchesIn(competitionId, spec.playDate, spec.timeInterval)).thenReturn(
            listOf(
                dataGenerator.newScheduleMatchDTO(competitionCategoryId = 3),
                dataGenerator.newScheduleMatchDTO(competitionCategoryId = 4),
                dataGenerator.newScheduleMatchDTO(competitionCategoryId = 7)
            )
        )

        // Act
        val result = trySchedule.execute(competitionId, spec, settings)

        // Assert
        Assertions.assertTrue(
            result.competitionCategoryIds.containsAll(listOf(3, 4, 7)),
            "Not the correct competition categories in the response"
        )

        Assertions.assertTrue(result.success, "Three 20-min matches should fit within 4 hours.")
        Assertions.assertEquals(spec.playDate, result.playDate, "Returned wrong play date")
        Assertions.assertEquals(spec.timeInterval, result.timeInterval, "Returned wrong time interval")
    }

    @Test
    fun whenNotAllMatchesFitInTimeInterval() {
        // Setup
        val competitionId = 1
        val spec = PreScheduleSpec(LocalDate.now(), TimeInterval.MORNING, 22)
        val settings = dataGenerator.newScheduleSettingsDTO(
            averageMatchTime = Duration.minutes(60),
            numberOfTables = 1,
        )
        `when`(mockedScheduleRepository.getMatchesIn(competitionId, spec.playDate, spec.timeInterval)).thenReturn(
            listOf(
                dataGenerator.newScheduleMatchDTO(competitionCategoryId = 3),
                dataGenerator.newScheduleMatchDTO(competitionCategoryId = 4),
                dataGenerator.newScheduleMatchDTO(competitionCategoryId = 7),
                dataGenerator.newScheduleMatchDTO(competitionCategoryId = 9),
                dataGenerator.newScheduleMatchDTO(competitionCategoryId = 11)
            )
        )

        // Act
        val result = trySchedule.execute(competitionId, spec, settings)

        // Assert
        Assertions.assertTrue(
            result.competitionCategoryIds.containsAll(listOf(3, 4, 7, 9, 11)),
            "Not the correct competition categories in the response"
        )

        Assertions.assertFalse(result.success, "Five 1-hour long matches cannot fit within a 4-hour interval on 1 table.")
        Assertions.assertEquals(spec.playDate, result.playDate, "Returned wrong play date")
        Assertions.assertEquals(spec.timeInterval, result.timeInterval, "Returned wrong time interval")
    }
}