package com.graphite.competitionplanner.schedule.domain

import com.graphite.competitionplanner.draw.service.MatchType
import com.graphite.competitionplanner.schedule.interfaces.IScheduleRepository
import com.graphite.competitionplanner.schedule.interfaces.MapMatchToTimeTableSlotSpec
import com.graphite.competitionplanner.util.DataGenerator
import com.graphite.competitionplanner.util.TestHelper
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mockito
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDateTime


@SpringBootTest
class TestCompetitionScheduler {

    private val mockedScheduleRepository: IScheduleRepository = Mockito.mock(IScheduleRepository::class.java)
    private val createSchedule = CreateSchedule()
    private val dataGenerator = DataGenerator()

    private val competitionScheduler = CompetitionScheduler(mockedScheduleRepository, createSchedule)

    @Test
    fun addingMatchToEmptySlot() {
        // Setup
        val spec = dataGenerator.newMapMatchToTimeTableSlotSpec(223, 100)
        Mockito.`when`(
            mockedScheduleRepository.addMatchToTimeTableSlot(spec)
        ).thenReturn(
            listOf(
                dataGenerator.newMatchToTimeTableSlot(matchId = spec.matchId, timeTableSlotId = spec.timeTableSlotId)
            )
        )

        // Act
        val timeTableSlot = competitionScheduler.mapMatchToTimeTableSlot(spec)

        // Assert
        Assertions.assertFalse(
            timeTableSlot.isDoubleBocked,
            "Only one match scheduled. Should not be marked as double booked"
        )
    }

    @Test
    fun addingMatchToOccupiedSlot() {
        // Setup
        val matchId = 223
        val matchId2 = 231
        val spec = dataGenerator.newMapMatchToTimeTableSlotSpec(matchId, 100)
        Mockito.`when`(
            mockedScheduleRepository.addMatchToTimeTableSlot(spec)
        ).thenReturn(
            listOf(
                dataGenerator.newMatchToTimeTableSlot(matchId = matchId, timeTableSlotId = spec.timeTableSlotId),
                dataGenerator.newMatchToTimeTableSlot(matchId = matchId2, timeTableSlotId = spec.timeTableSlotId)
            )
        )

        // Act
        val timeTableSlot = competitionScheduler.mapMatchToTimeTableSlot(spec)

        // Assert
        Assertions.assertTrue(
            timeTableSlot.isDoubleBocked,
            "Two matches in same slot. Should be marked as double booked"
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

    @Test
    fun something() {
        // "Schemalägg Gruppspelsmatcher för Herrar 2 på bord 4-7 med start kl 09:00 den 20/5 2022 i Hall A"
        val competitionId = 12
        val competitionCategoryId = 123
        val matchType = MatchType.GROUP
        val tables = listOf(1, 2, 3)
        val startTime = LocalDateTime.now()
        val location = "Lundaparken A"

        competitionScheduler.scheduleCompetitionCategory(competitionId, competitionCategoryId, matchType, tables, startTime, location)
    }

    @Test
    fun testGetCompetitionSchedule() {
        // Setup
        val competitionId = 192
        val now = LocalDateTime.now()
        val matchesToSlots = listOf(
            dataGenerator.newTimeTableSlotToMatch(id = 1, startTime = now, tableNumber = 1, matchInfo = dataGenerator.newTimeTableMatchInfo(id = 2)),
            dataGenerator.newTimeTableSlotToMatch(id = 2, startTime = now, tableNumber = 2, matchInfo = dataGenerator.newTimeTableMatchInfo(id = 3)),
            dataGenerator.newTimeTableSlotToMatch(id = 2, startTime = now, tableNumber = 2, matchInfo = dataGenerator.newTimeTableMatchInfo(id = 4)),
            dataGenerator.newTimeTableSlotToMatch(id = 3, startTime = now, tableNumber = 3, matchInfo = null),
            dataGenerator.newTimeTableSlotToMatch(id = 4, startTime = now, tableNumber = 4, matchInfo = dataGenerator.newTimeTableMatchInfo(id = 5))
        )
        Mockito.`when`(mockedScheduleRepository.getTimeTable(competitionId)).thenReturn(matchesToSlots)

        // Act
        val schedule = competitionScheduler.getSchedule(competitionId)

        // Assert
        Assertions.assertEquals(4, schedule.size, "Not the correct number of TimeTableSlots")

        Assertions.assertFalse(schedule.first { it.id == 1 }.isDoubleBocked, "Not expecting a double booking in first TimeTableSlot")
        Assertions.assertEquals(1, schedule.first { it.id == 1 }.matchInfo.size, "Not the expected number of match infos in TimeTableSlot")

        Assertions.assertTrue(schedule.first { it.id == 2 }.isDoubleBocked, "Expected a double booking in second TimeTableSlot")
        Assertions.assertEquals(2, schedule.first { it.id == 2 }.matchInfo.size, "Not the expected number of match infos in TimeTableSlot")

        Assertions.assertFalse(schedule.first { it.id == 3 }.isDoubleBocked, "Not expecting a double booking")
        Assertions.assertEquals(0, schedule.first { it.id == 3 }.matchInfo.size, "Not the expected number of match infos in TimeTableSlot")

        Assertions.assertFalse(schedule.first { it.id == 4 }.isDoubleBocked, "Not expecting a double booking")
        Assertions.assertEquals(1, schedule.first { it.id == 4 }.matchInfo.size, "Not the expected number of match infos in TimeTableSlot")
    }

    @Test
    fun testGetCompetitionScheduleSortOrder() {
        // Setup
        val competitionId = 192
        val now = LocalDateTime.now()
        val matchesToSlots = listOf(
            dataGenerator.newTimeTableSlotToMatch(id = 1, startTime = now, tableNumber = 2, location = "A"),
            dataGenerator.newTimeTableSlotToMatch(id = 2, startTime = now, tableNumber = 2, location = "B"),
            dataGenerator.newTimeTableSlotToMatch(id = 3, startTime = now, tableNumber = 1, location = "A"),
            dataGenerator.newTimeTableSlotToMatch(id = 4, startTime = now, tableNumber = 1, location = "B"),
            dataGenerator.newTimeTableSlotToMatch(id = 5, startTime = now.plusMinutes(10), tableNumber = 2, location = "A"),
            dataGenerator.newTimeTableSlotToMatch(id = 6, startTime = now.plusMinutes(10), tableNumber = 2, location = "B"),
            dataGenerator.newTimeTableSlotToMatch(id = 7, startTime = now.plusMinutes(10), tableNumber = 1, location = "A"),
            dataGenerator.newTimeTableSlotToMatch(id = 8, startTime = now.plusMinutes(10), tableNumber = 1, location = "B"),
            dataGenerator.newTimeTableSlotToMatch(id = 9, startTime = now.plusMinutes(30), tableNumber = 2, location = "A"),
            dataGenerator.newTimeTableSlotToMatch(id = 10, startTime = now.plusMinutes(30), tableNumber = 2, location = "B"),
            dataGenerator.newTimeTableSlotToMatch(id = 11, startTime = now.plusMinutes(20), tableNumber = 1, location = "A"),
            dataGenerator.newTimeTableSlotToMatch(id = 12, startTime = now.plusMinutes(20), tableNumber = 1, location = "B"),
        )
        Mockito.`when`(mockedScheduleRepository.getTimeTable(competitionId)).thenReturn(matchesToSlots)

        // TimeTableSlot ids if sorted by location -> start time -> table number
        val expectedOrder = listOf(3, 1, 7, 5, 11, 9, 4, 2, 8, 6, 12, 10)

        // Act
        val schedule = competitionScheduler.getSchedule(competitionId)

        // Assert
        val timeTableSlotIds = schedule.map { it.id }
        Assertions.assertEquals(expectedOrder, timeTableSlotIds)
    }


    @Captor
    lateinit var classCaptor: ArgumentCaptor<List<MapMatchToTimeTableSlotSpec>>

    @Test
    fun appendMatchesToTables() {
        // Setup
        val competitionId = 192
        val now = LocalDateTime.now()
        val matchesToSlots = listOf(
            dataGenerator.newTimeTableSlotToMatch(id = 1, startTime = now, tableNumber = 1, matchInfo = dataGenerator.newTimeTableMatchInfo(id = 2)),
            dataGenerator.newTimeTableSlotToMatch(id = 2, startTime = now, tableNumber = 2, matchInfo = dataGenerator.newTimeTableMatchInfo(id = 4)),
            dataGenerator.newTimeTableSlotToMatch(id = 3, startTime = now, tableNumber = 3, matchInfo = null),
            dataGenerator.newTimeTableSlotToMatch(id = 4, startTime = now, tableNumber = 4, matchInfo = null),
            dataGenerator.newTimeTableSlotToMatch(id = 5, startTime = now.plusMinutes(25), tableNumber = 1, matchInfo = null),
            dataGenerator.newTimeTableSlotToMatch(id = 6, startTime = now.plusMinutes(25), tableNumber = 2, matchInfo = null),
            dataGenerator.newTimeTableSlotToMatch(id = 7, startTime = now.plusMinutes(25), tableNumber = 3, matchInfo = null),
            dataGenerator.newTimeTableSlotToMatch(id = 8, startTime = now.plusMinutes(25), tableNumber = 4, matchInfo = null),
            dataGenerator.newTimeTableSlotToMatch(id = 9, startTime = now.plusMinutes(50), tableNumber = 1, matchInfo = null),
            dataGenerator.newTimeTableSlotToMatch(id = 10, startTime = now.plusMinutes(50), tableNumber = 2, matchInfo = null),
            dataGenerator.newTimeTableSlotToMatch(id = 11, startTime = now.plusMinutes(50), tableNumber = 3, matchInfo = null),
            dataGenerator.newTimeTableSlotToMatch(id = 12, startTime = now.plusMinutes(50), tableNumber = 4, matchInfo = null)
        )
        Mockito.`when`(mockedScheduleRepository.getTimeTable(competitionId)).thenReturn(matchesToSlots)

        val competitionCategoryId = 13
        val matches = dataGenerator.pool1(competitionCategoryId)
        Mockito.`when`(mockedScheduleRepository.getScheduleMatches(competitionCategoryId, MatchType.GROUP)).thenReturn(matches)

        // Act
        competitionScheduler.appendMatchesToTables(competitionId, 13, MatchType.GROUP, listOf(1,2,3,4), "Hall A")

        // Record the spec sent to the repository for validation
        Mockito.verify(mockedScheduleRepository).updateMatchesTimeTablesSlots(TestHelper.MockitoHelper.capture(classCaptor))
        val result = classCaptor.value as List<MapMatchToTimeTableSlotSpec>

        Assertions.assertNotNull(result)
        Assertions.assertEquals(matches.size, result.size, "Wrong number of matches got scheduled")
        Assertions.assertEquals(matches.size, result.map { it.matchId }.distinct().size, "At least one match did not get scheduled")

        assertTimeSlotGotBooked(result, 3)
        assertTimeSlotGotBooked(result, 4)
        assertTimeSlotGotBooked(result, 5)
        assertTimeSlotGotBooked(result, 6)
        assertTimeSlotGotBooked(result, 9)
        assertTimeSlotGotBooked(result, 10)
    }

    private fun assertTimeSlotGotBooked(timeslots: List<MapMatchToTimeTableSlotSpec>, timeslotId: Int) {
        val slot3 = timeslots.filter { it.timeTableSlotId == timeslotId}
        Assertions.assertEquals(1, slot3.size, "Expected only one match to be mapped to this slot")
    }

    @Test
    fun shouldThrowIndexOutOfBoundExceptionWhenNotAllMatchesFit() {
        // Setup
        val competitionId = 192
        val now = LocalDateTime.now()
        val matchesToSlots = listOf(
            dataGenerator.newTimeTableSlotToMatch(id = 1, startTime = now, tableNumber = 1, matchInfo = dataGenerator.newTimeTableMatchInfo(id = 2)),
            dataGenerator.newTimeTableSlotToMatch(id = 2, startTime = now, tableNumber = 2, matchInfo = dataGenerator.newTimeTableMatchInfo(id = 4)),
            dataGenerator.newTimeTableSlotToMatch(id = 3, startTime = now, tableNumber = 3, matchInfo = null),
            dataGenerator.newTimeTableSlotToMatch(id = 4, startTime = now, tableNumber = 4, matchInfo = null),
        )
        Mockito.`when`(mockedScheduleRepository.getTimeTable(competitionId)).thenReturn(matchesToSlots)

        val competitionCategoryId = 13
        val matches = dataGenerator.pool1(competitionCategoryId)
        Mockito.`when`(mockedScheduleRepository.getScheduleMatches(competitionCategoryId, MatchType.GROUP)).thenReturn(matches)

        // Act
        Assertions.assertThrows(IndexOutOfBoundsException::class.java) {
            competitionScheduler.appendMatchesToTables(competitionId, 13, MatchType.GROUP, listOf(1,2,3,4), "Hall A")
        }
    }
}