package com.graphite.competitionplanner.schedule.domain

import com.graphite.competitionplanner.schedule.interfaces.IScheduleRepository
import com.graphite.competitionplanner.util.DataGenerator
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.boot.test.context.SpringBootTest


@SpringBootTest
class TestModifyTimeTable {

    private val mockedScheduleRepository: IScheduleRepository = Mockito.mock(IScheduleRepository::class.java)
    private val dataGenerator = DataGenerator()

    private val modifyTimeTable = ModifyTimeTable(mockedScheduleRepository)

    @Test
    fun addingMatchToEmptySlot() {
        // Setup
        val timeSlotId = 100
        val matchId = 223
        Mockito.`when`(
            mockedScheduleRepository.addMatchToTimeTableSlot(
                timeSlotId,
                matchId
            )
        ).thenReturn(
            listOf(
                dataGenerator.newMatchToTimeTableSlot(matchId = matchId, timeTableSlotId = timeSlotId)
            )
        )

        // Act
        val timeTableSlot = modifyTimeTable.addMatchToTimeTableSlot(timeSlotId, matchId)

        // Assert
        Assertions.assertFalse(
            timeTableSlot.isDoubleBocked,
            "Only one match scheduled. Should not be marked as double booked"
        )
    }

    @Test
    fun addingMatchToOccupiedSlot() {
        // Setup
        val timeSlotId = 100
        val matchId = 223
        val matchId2 = 231
        Mockito.`when`(
            mockedScheduleRepository.addMatchToTimeTableSlot(
                timeSlotId,
                matchId
            )
        ).thenReturn(
            listOf(
                dataGenerator.newMatchToTimeTableSlot(matchId = matchId, timeTableSlotId = timeSlotId),
                dataGenerator.newMatchToTimeTableSlot(matchId = matchId2, timeTableSlotId = timeSlotId)
            )
        )

        // Act
        val timeTableSlot = modifyTimeTable.addMatchToTimeTableSlot(timeSlotId, matchId)

        // Assert
        Assertions.assertTrue(
            timeTableSlot.isDoubleBocked,
            "Two matches in same slow. Should be marked as double booked"
        )
        Assertions.assertEquals(
            2,
            timeTableSlot.matchInfo.size,
            "Not the expected number of match informations in the TimeTableSlot "
        )
        Assertions.assertTrue(
            timeTableSlot.matchInfo.map { it.id }.containsAll(listOf(matchId, matchId2)),
            "Not the expected match IDs returned"
        )
    }
}