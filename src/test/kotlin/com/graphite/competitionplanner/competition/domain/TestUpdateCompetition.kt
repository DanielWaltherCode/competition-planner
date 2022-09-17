package com.graphite.competitionplanner.competition.domain

import com.graphite.competitionplanner.competition.interfaces.ICompetitionRepository
import com.graphite.competitionplanner.schedule.domain.TimeTableSlotHandler
import com.graphite.competitionplanner.schedule.repository.ScheduleRepository
import com.graphite.competitionplanner.schedule.service.AvailableTablesService
import com.graphite.competitionplanner.schedule.service.DailyStartEndService
import com.graphite.competitionplanner.util.DataGenerator
import com.graphite.competitionplanner.util.TestHelper
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestUpdateCompetition {

    val dataGenerator = DataGenerator()
    private final val mockedRepository = Mockito.mock(ICompetitionRepository::class.java)
    private final val mockedTimeTableHandler = Mockito.mock(TimeTableSlotHandler::class.java)
    private final val mockedStartEnd = Mockito.mock(DailyStartEndService::class.java)
    private final val mockedAvailableTables = Mockito.mock(AvailableTablesService::class.java)
    private final val mockedScheduleRepository = Mockito.mock(ScheduleRepository::class.java)
    val updateCompetition: UpdateCompetition = Mockito.spy(
        UpdateCompetition(
            mockedRepository,
            mockedTimeTableHandler,
            mockedStartEnd,
            mockedAvailableTables,
            mockedScheduleRepository)
    )

    @Test
    fun shouldDelegateToRepository() {
        // Setup
        val competitionId = 1334
        val spec = dataGenerator.newCompetitionUpdateSpec()
        Mockito.doNothing().`when`(updateCompetition).updateThingsDependentOnCompetitionDays(competitionId, spec)
        // Act

        updateCompetition.execute(competitionId, spec)

        // Assert
        Mockito.verify(mockedRepository, Mockito.times(1)).update(competitionId, spec)
        Mockito.verify(mockedRepository, Mockito.times(1)).update(Mockito.anyInt(), TestHelper.MockitoHelper.anyObject())
    }
}