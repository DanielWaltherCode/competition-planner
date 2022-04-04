package com.graphite.competitionplanner.schedule.domain

import com.graphite.competitionplanner.schedule.interfaces.IScheduleRepository
import com.graphite.competitionplanner.util.DataGenerator
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDate


@SpringBootTest
class TestTrySchedule {

    private val mockedScheduleRepository: IScheduleRepository = Mockito.mock(IScheduleRepository::class.java)
    private val trySchedule = TrySchedule(mockedScheduleRepository)

    private val dataGenerator = DataGenerator()

    @Test
    fun something() {
        // Setup
        val competitionId = 1
        val spec = PreScheduleSpec(LocalDate.now(), TimeInterval.MORNING, 22)
        val settings = dataGenerator.newScheduleSettingsDTO()


        // Act
        val result = trySchedule.execute(competitionId, spec, settings)

        // Act
        Assertions.assertNotNull(result)
        Assertions.assertEquals(spec.playDate, result.playDate)
        Assertions.assertEquals(spec.timeInterval, result.timeInterval)
    }

    @Test
    fun resultShouldHaveCompetitionCategoriesInSameTimeslot() {
        // Setup
        val competitionId = 1
        val spec = PreScheduleSpec(LocalDate.now(), TimeInterval.MORNING, 22)
        val settings = dataGenerator.newScheduleSettingsDTO()
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
    }
}