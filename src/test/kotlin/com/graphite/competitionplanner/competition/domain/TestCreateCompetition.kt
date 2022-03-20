package com.graphite.competitionplanner.competition.domain

import com.graphite.competitionplanner.club.domain.FindClub
import com.graphite.competitionplanner.common.exception.NotFoundException
import com.graphite.competitionplanner.competition.interfaces.ICompetitionRepository
import com.graphite.competitionplanner.schedule.api.AvailableTablesWholeCompetitionSpec
import com.graphite.competitionplanner.schedule.service.AvailableTablesService
import com.graphite.competitionplanner.schedule.service.DailyStartEndService
import com.graphite.competitionplanner.schedule.service.ScheduleMetadataService
import com.graphite.competitionplanner.util.DataGenerator
import com.graphite.competitionplanner.util.TestHelper
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestCreateCompetition {

    val dataGenerator = DataGenerator()
    private val mockedFindClub = mock(FindClub::class.java)
    private val mockedRepository = mock(ICompetitionRepository::class.java)
    private val mockedScheduleMetadataService = mock(ScheduleMetadataService::class.java)
    private val mockedDailyStartEndService = mock(DailyStartEndService::class.java)
    private val mockedAvailableTablesService = mock(AvailableTablesService::class.java)
    val createCompetition = CreateCompetition(
        mockedRepository, mockedFindClub, mockedScheduleMetadataService, mockedDailyStartEndService,
        mockedAvailableTablesService
    )

    @Test
    fun shouldStoreCompetitionIfEntityIsOk() {
        // Setup
        val dto = dataGenerator.newCompetitionSpec()
        `when`(mockedFindClub.byId(dto.organizingClubId))
            .thenReturn(dataGenerator.newClubDTO(id = dto.organizingClubId))
        `when`(mockedRepository.store(TestHelper.MockitoHelper.anyObject())).thenReturn(
            dataGenerator.newCompetitionDTO(id = 1, organizingClubId = dto.organizingClubId)
        )

        // Act
        createCompetition.execute(dto)

        // Assert
        verify(mockedRepository, times(1)).store(TestHelper.MockitoHelper.anyObject())
    }

    @Test
    fun shouldAssertThatClubExist() {
        // Setup
        val dto = dataGenerator.newCompetitionSpec()
        `when`(mockedFindClub.byId(dto.organizingClubId))
            .thenReturn(dataGenerator.newClubDTO(id = dto.organizingClubId))
        `when`(mockedRepository.store(TestHelper.MockitoHelper.anyObject())).thenReturn(
            dataGenerator.newCompetitionDTO(id = 1, organizingClubId = dto.organizingClubId)
        )

        // Act
        createCompetition.execute(dto)

        // Assert
        verify(mockedFindClub, atLeastOnce()).byId(dto.organizingClubId)
    }

    @Test
    fun shouldNotStoreCompetitionIfClubDoesNotExist() {
        // Setup
        val dto = dataGenerator.newCompetitionSpec()
        `when`(mockedFindClub.byId(dto.organizingClubId)).thenThrow(NotFoundException(""))

        // Act
        Assertions.assertThrows(NotFoundException::class.java) { createCompetition.execute(dto) }

        // Assert
        verify(mockedRepository, never()).store(TestHelper.MockitoHelper.anyObject())
    }

    @Test
    fun whenCreatingCompetitionDefaultSchedulingDataShallBeSet() {
        // Setup
        val spec = dataGenerator.newCompetitionSpec()
        val dto = dataGenerator.newCompetitionDTO()
        `when`(mockedRepository.store(spec)).thenReturn(dto)

        // Act
        createCompetition.execute(spec)

        // Assert
        verify(mockedScheduleMetadataService, times(1)).addDefaultScheduleMetadata(dto.id)
        verify(mockedScheduleMetadataService, times(1)).addDefaultScheduleMetadata(anyInt())
    }

    @Test
    fun whenCreatingCompetitionDailyStartAndEndTimesShallBeSet() {
        // Setup
        val spec = dataGenerator.newCompetitionSpec()
        val dto = dataGenerator.newCompetitionDTO()
        `when`(mockedRepository.store(spec)).thenReturn(dto)

        // Act
        createCompetition.execute(spec)

        // Assert
        verify(mockedDailyStartEndService, times(1)).addDailyStartAndEndForWholeCompetition(dto.id)
        verify(mockedDailyStartEndService, times(1)).addDailyStartAndEndForWholeCompetition(anyInt())
    }

    @Test
    fun whenCreatingCompetitionAvailableTablesShouldBeSetToZero() {
        // Setup
        val spec = dataGenerator.newCompetitionSpec()
        val dto = dataGenerator.newCompetitionDTO()
        `when`(mockedRepository.store(spec)).thenReturn(dto)

        // Act
        createCompetition.execute(spec)

        // Assert
        verify(mockedAvailableTablesService, times(1)).registerTablesAvailableForWholeCompetition(
            dto.id,
            AvailableTablesWholeCompetitionSpec(0)
        )
        verify(mockedAvailableTablesService, times(1)).registerTablesAvailableForWholeCompetition(
            anyInt(),
            TestHelper.MockitoHelper.anyObject()
        )
    }
}