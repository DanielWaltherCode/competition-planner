package com.graphite.competitionplanner.competition.service

import com.graphite.competitionplanner.competition.domain.CreateCompetition
import com.graphite.competitionplanner.competition.domain.FindCompetitions
import com.graphite.competitionplanner.schedule.api.AvailableTablesWholeCompetitionSpec
import com.graphite.competitionplanner.schedule.service.ScheduleService
import com.graphite.competitionplanner.util.DataGenerator
import com.graphite.competitionplanner.util.TestHelper
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestCompetitionsService {

    private val mockedCreateCompetition = mock(CreateCompetition::class.java)
    private val mockedFindCompetitions = mock(FindCompetitions::class.java)
    private val mockedSchedulingService = mock(ScheduleService::class.java)
    private val service = CompetitionService(
        mockedSchedulingService,
        mockedFindCompetitions,
        mockedCreateCompetition
    )

    val dataGenerator = DataGenerator()

    @Test
    fun shouldCallCreateUseCase() {
        // Setup
        val spec = dataGenerator.newCompetitionSpec()
        `when`(mockedCreateCompetition.execute(spec)).thenReturn(dataGenerator.newCompetitionDTO())

        // Act
        service.addCompetition(spec)

        // Assert
        verify(mockedCreateCompetition, times(1)).execute(spec)
        verify(mockedCreateCompetition, times(1)).execute(TestHelper.MockitoHelper.anyObject())
    }

    @Test
    fun whenCreatingCompetitionDefaultSchedulingDataShallBeSet() {
        // Setup
        val spec = dataGenerator.newCompetitionSpec()
        val dto = dataGenerator.newCompetitionDTO()
        `when`(mockedCreateCompetition.execute(spec)).thenReturn(dto)

        // Act
        service.addCompetition(spec)

        // Assert
        verify(mockedSchedulingService, times(1)).addDefaultScheduleMetadata(dto.id)
        verify(mockedSchedulingService, times(1)).addDefaultScheduleMetadata(anyInt())
    }

    @Test
    fun whenCreatingCompetitionDailyStartAndEndTimesShallBeSet() {
        // Setup
        val spec = dataGenerator.newCompetitionSpec()
        val dto = dataGenerator.newCompetitionDTO()
        `when`(mockedCreateCompetition.execute(spec)).thenReturn(dto)

        // Act
        service.addCompetition(spec)

        // Assert
        verify(mockedSchedulingService, times(1)).addDailyStartAndEndForWholeCompetition(dto.id)
        verify(mockedSchedulingService, times(1)).addDailyStartAndEndForWholeCompetition(anyInt())
    }

    @Test
    fun whenCreatingCompetitionAvailableTablesShouldBeSetToZero() {
        // Setup
        val spec = dataGenerator.newCompetitionSpec()
        val dto = dataGenerator.newCompetitionDTO()
        `when`(mockedCreateCompetition.execute(spec)).thenReturn(dto)

        // Act
        service.addCompetition(spec)

        // Assert
        verify(mockedSchedulingService, times(1)).registerTablesAvailableForWholeCompetition(
            dto.id,
            AvailableTablesWholeCompetitionSpec(0)
        )
        verify(mockedSchedulingService, times(1)).registerTablesAvailableForWholeCompetition(
            anyInt(),
            TestHelper.MockitoHelper.anyObject()
        )
    }
}