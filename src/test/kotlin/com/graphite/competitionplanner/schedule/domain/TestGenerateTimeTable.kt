package com.graphite.competitionplanner.schedule.domain

import com.graphite.competitionplanner.schedule.interfaces.IScheduleRepository
import com.graphite.competitionplanner.util.DataGenerator
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDate
import java.util.concurrent.TimeUnit
import kotlin.time.toDuration

@SpringBootTest
class TestGenerateTimeTable {

    private val mockedScheduleRepository: IScheduleRepository = Mockito.mock(IScheduleRepository::class.java)
    private val generateTimeTable = GenerateTimeTable(mockedScheduleRepository)
    private val dataGenerator = DataGenerator()

    @Test
    fun testGenerateTimeTables() {
        // Setup
        val competitionId = 123
        val numberOfTables = 12
        val estimatedMatchTime = 25.toDuration(TimeUnit.MINUTES)
        val location = "Arena A"
        val dates = listOf(LocalDate.of(2022, 6, 1), LocalDate.of(2022, 6, 2))

        // Act
        generateTimeTable.execute(competitionId, numberOfTables, estimatedMatchTime, location, dates)
    }
}