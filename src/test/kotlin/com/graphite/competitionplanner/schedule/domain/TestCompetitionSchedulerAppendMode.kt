package com.graphite.competitionplanner.schedule.domain

import com.graphite.competitionplanner.competition.domain.FindCompetitions
import com.graphite.competitionplanner.competitioncategory.domain.GetCompetitionCategories
import com.graphite.competitionplanner.draw.interfaces.Round
import com.graphite.competitionplanner.draw.service.DrawService
import com.graphite.competitionplanner.match.domain.MatchType
import com.graphite.competitionplanner.schedule.interfaces.IScheduleRepository
import com.graphite.competitionplanner.schedule.interfaces.MapMatchToTimeTableSlotSpec
import com.graphite.competitionplanner.schedule.interfaces.TimeTableSlotMatchInfo
import com.graphite.competitionplanner.schedule.interfaces.TimeTableSlotToMatch
import com.graphite.competitionplanner.schedule.service.AvailableTablesService
import com.graphite.competitionplanner.util.DataGenerator
import com.graphite.competitionplanner.util.TestHelper
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit
import kotlin.time.Duration
import kotlin.time.toDuration

@SpringBootTest
class TestCompetitionSchedulerAppendMode {

    private val mockedScheduleRepository: IScheduleRepository = Mockito.mock(IScheduleRepository::class.java)
    private val mockedAvailableTables = Mockito.mock(AvailableTablesService::class.java)
    private val mockedFindCompetitions = Mockito.mock(FindCompetitions::class.java)
    private val mockedDrawService = Mockito.mock(DrawService::class.java)
    private val mockedGetCompetitionCategories = Mockito.mock(GetCompetitionCategories::class.java)
    private val createSchedule = CreateSchedule()

    private val competitionScheduler = CompetitionScheduler(
        mockedScheduleRepository,
        createSchedule,
        mockedFindCompetitions,
        mockedAvailableTables,
        mockedDrawService,
        mockedGetCompetitionCategories
    )

    private val dataGenerator = DataGenerator()

    @Captor
    lateinit var classCaptor: ArgumentCaptor<List<MapMatchToTimeTableSlotSpec>>

    /**
     * This is a scenario when the user request 4 tables, but due to scheduling constraints only two
     * tables can be used in parallell. Due to not scheduling a player in two matches simultaneously
     */
    @Test
    fun whenFewerTablesAreUsedThanRequested(){
        // Setup
        val competitionId = 12
        `when`(mockedFindCompetitions.byId(any())).thenReturn(dataGenerator.newCompetitionDTO(competitionId))

        val competitionCategoryId = 123
        val startTime = LocalDateTime.now()
        val matchSchedulerSpec = dataGenerator.newMatchSchedulerSpec(
            MatchType.GROUP, listOf(1, 2, 3), startTime.toLocalDate(), startTime.toLocalTime()
        )

        val matchesToSchedule = dataGenerator.pool1(competitionCategoryId)
        `when`(mockedScheduleRepository.getScheduleMatches(any(), any())).thenReturn(matchesToSchedule)

        val duration = 25.toDuration(TimeUnit.MINUTES)
        val timeTable = generateTimeTable(startTime, duration, 4, 4)
            .mapIndexed{ index, it -> it.setId(index + 1) }

        `when`(mockedScheduleRepository.getTimeTable(competitionId)).thenReturn(timeTable)

        // Act
        competitionScheduler.scheduleCompetitionCategory(
            competitionId,
            competitionCategoryId,
            matchSchedulerSpec
        )

        // Record the spec sent to the repository for validation
        Mockito.verify(mockedScheduleRepository).updateMatchesTimeTablesSlots(TestHelper.MockitoHelper.capture(classCaptor))
        val result = classCaptor.value as List<MapMatchToTimeTableSlotSpec>

        // Assert
        Assertions.assertNotNull(result)
        Assertions.assertEquals(matchesToSchedule.size, result.size,
            "Not the correct number of matches scheduled.")
        Assertions.assertEquals(matchesToSchedule.size, result.map { it.matchId }.distinct().size,
            "Not all matches were scheduled")
        Assertions.assertEquals(matchesToSchedule.size, result.map { it.timeTableSlotId }.distinct().size,
            "Not unique timeslots where used")

        Assertions.assertEquals(listOf(1, 2, 5, 6, 9, 10), result.map { it.timeTableSlotId }.sorted(),
            "Expected the ${matchesToSchedule.size} matches to be scheduled tables 1 & 2 on three consecutive " +
                    "timeslots\n" + timeTable.printOut())

    }

    /**
     * This scenario a user request to use 3 tables to schedule 2 pools (totally 12 matches). In theory, we could
     * schedule 4 matches (2 for each pool) in parallel, but we won't do that.
     */
    @Test
    fun whenFewerTablesAreRequestedThanWeCanOptimallyUtilized() {
        // Setup
        val competitionId = 12
        `when`(mockedFindCompetitions.byId(any())).thenReturn(dataGenerator.newCompetitionDTO(competitionId))

        val competitionCategoryId = 123
        val startTime = LocalDateTime.now()
        val matchSchedulerSpec = dataGenerator.newMatchSchedulerSpec(
            MatchType.GROUP, listOf(1, 2, 4), startTime.toLocalDate(), startTime.toLocalTime()
        )

        val matchesToSchedule = dataGenerator.pool1(competitionCategoryId) + dataGenerator.pool2(competitionCategoryId)
        `when`(mockedScheduleRepository.getScheduleMatches(any(), any())).thenReturn(matchesToSchedule)

        val duration = 25.toDuration(TimeUnit.MINUTES)
        val timeTable = generateTimeTable(startTime, duration, 8, 4)
            .mapIndexed{ index, it -> it.setId(index + 1) }

        `when`(mockedScheduleRepository.getTimeTable(competitionId)).thenReturn(timeTable)

        // Act
        competitionScheduler.scheduleCompetitionCategory(
            competitionId,
            competitionCategoryId,
            matchSchedulerSpec
        )

        // Record the spec sent to the repository for validation
        Mockito.verify(mockedScheduleRepository).updateMatchesTimeTablesSlots(TestHelper.MockitoHelper.capture(classCaptor))
        val result = classCaptor.value as List<MapMatchToTimeTableSlotSpec>

        // Assert
        Assertions.assertNotNull(result)
        Assertions.assertEquals(matchesToSchedule.size, result.size,
            "Not the correct number of matches scheduled.")
        Assertions.assertEquals(matchesToSchedule.size, result.map { it.matchId }.distinct().size,
            "Not all matches were scheduled")
        Assertions.assertEquals(matchesToSchedule.size, result.map { it.timeTableSlotId }.distinct().size,
            "Not unique timeslots where used")

        Assertions.assertEquals(
            listOf(1, 2, 4, 5, 6, 8, 9, 10, 12, 13, 14, 16),
            result.map { it.timeTableSlotId }.sorted(),
            "Expected the ${matchesToSchedule.size} matches to be scheduled on tables "
                    + matchSchedulerSpec.tableNumbers.joinToString { it.toString() } + "\n" +
                    timeTable.printOut())
    }

    /**
     * This scenario is happening when user selects a table that already has scheduled matches. In other words,
     * some tables selected are occupied.
     */
    @Test
    fun whenSomeOfTheSelectedTablesAreOccupied() {
        // Setup
        val competitionId = 12
        `when`(mockedFindCompetitions.byId(any())).thenReturn(dataGenerator.newCompetitionDTO(competitionId))

        val competitionCategoryId = 123
        val startTime = LocalDateTime.now()
        val matchSchedulerSpec = dataGenerator.newMatchSchedulerSpec(
            MatchType.GROUP, listOf(1, 2, 4), startTime.toLocalDate(), startTime.toLocalTime()
        )

        val matchesToSchedule = dataGenerator.pool1(competitionCategoryId) + dataGenerator.pool2(competitionCategoryId)
        `when`(mockedScheduleRepository.getScheduleMatches(any(), any())).thenReturn(matchesToSchedule)

        val duration = 25.toDuration(TimeUnit.MINUTES)
        val timeTable = generateTimeTable(startTime, duration, 8, 4)
            .mapIndexed{ index, it -> it.setId(index + 1) }
            .map { if (it.id % 4 == 1) it.Occupy() else it } // Occupies table number 1

        `when`(mockedScheduleRepository.getTimeTable(competitionId)).thenReturn(timeTable)

        // Act
        competitionScheduler.scheduleCompetitionCategory(
            competitionId,
            competitionCategoryId,
            matchSchedulerSpec
        )

        // Record the spec sent to the repository for validation
        Mockito.verify(mockedScheduleRepository).updateMatchesTimeTablesSlots(TestHelper.MockitoHelper.capture(classCaptor))
        val result = classCaptor.value as List<MapMatchToTimeTableSlotSpec>

        // Assert
        Assertions.assertNotNull(result)
        Assertions.assertEquals(matchesToSchedule.size, result.size,
            "Not the correct number of matches scheduled.")
        Assertions.assertEquals(matchesToSchedule.size, result.map { it.matchId }.distinct().size,
            "Not all matches were scheduled")
        Assertions.assertEquals(matchesToSchedule.size, result.map { it.timeTableSlotId }.distinct().size,
            "Not unique timeslots where used")

        Assertions.assertEquals(
            listOf(2, 4, 6, 8, 10, 12, 14, 16, 18, 20, 22, 24),
            result.map { it.timeTableSlotId }.sorted(),
            "Expected the ${matchesToSchedule.size} matches to be scheduled on tables "
                    + "2 and 4" + "\n" +
                    timeTable.printOut())
    }

    /**
     * In this scenario we have a varying number of available table over time. In this case we have 12 matches
     * from 2 pools which means we can schedule at most 4 matches in parallel. However, for the first 3 time slots
     * only 2 tables are available.
     */
    @Test
    fun whenThereIsVaryingNumberOfAvailableTables() {
        // Setup
        val competitionId = 12
        `when`(mockedFindCompetitions.byId(any())).thenReturn(dataGenerator.newCompetitionDTO(competitionId))

        val competitionCategoryId = 123
        val startTime = LocalDateTime.now()
        val matchSchedulerSpec = dataGenerator.newMatchSchedulerSpec(
            MatchType.GROUP, listOf(1, 2, 3, 4), startTime.toLocalDate(), startTime.toLocalTime()
        )

        val matchesToSchedule = dataGenerator.pool1(competitionCategoryId) + dataGenerator.pool2(competitionCategoryId)
        `when`(mockedScheduleRepository.getScheduleMatches(any(), any())).thenReturn(matchesToSchedule)

        val duration = 25.toDuration(TimeUnit.MINUTES)
        val timeTable = generateTimeTable(startTime, duration, 8, 4)
            .mapIndexed{ index, it -> it.setId(index + 1) }
            // Occupies table number 1 & 2 in the first 3 timeslots
            .map { if ((it.id % 4 == 1 || it.id % 4 == 2) && it.id < 12 ) it.Occupy() else it }


        `when`(mockedScheduleRepository.getTimeTable(competitionId)).thenReturn(timeTable)

        // Act
        competitionScheduler.scheduleCompetitionCategory(
            competitionId,
            competitionCategoryId,
            matchSchedulerSpec
        )

        // Record the spec sent to the repository for validation
        Mockito.verify(mockedScheduleRepository).updateMatchesTimeTablesSlots(TestHelper.MockitoHelper.capture(classCaptor))
        val result = classCaptor.value as List<MapMatchToTimeTableSlotSpec>

        // Assert
        Assertions.assertNotNull(result)
        Assertions.assertEquals(matchesToSchedule.size, result.size,
            "Not the correct number of matches scheduled.")
        Assertions.assertEquals(matchesToSchedule.size, result.map { it.matchId }.distinct().size,
            "Not all matches were scheduled")
        Assertions.assertEquals(matchesToSchedule.size, result.map { it.timeTableSlotId }.distinct().size,
            "Not unique timeslots where used")

        Assertions.assertEquals(
            listOf(3, 4, 7, 8, 11, 12, 13, 14, 15, 16, 17, 18),
            result.map { it.timeTableSlotId }.sorted(),
            "Expected the ${matchesToSchedule.size} matches to be scheduled on tables 3 and 4 for the first"
                    + " 3 time slots, then all 4 tables would be used for the remaining matches\n" +
                    timeTable.printOut())
    }

    @Test
    fun whenUserSpecifiesStartTime() {
        // Setup
        val competitionId = 12
        `when`(mockedFindCompetitions.byId(any())).thenReturn(dataGenerator.newCompetitionDTO(competitionId))

        val competitionCategoryId = 123
        val duration = 25.toDuration(TimeUnit.MINUTES)
        val startTime = LocalDateTime.now()
        val userSelectedStartTime = startTime.plusMinutes(duration.inWholeMinutes * 3) // Skip first 2 time slots
        val matchSchedulerSpec = dataGenerator.newMatchSchedulerSpec(
            MatchType.GROUP, listOf(1, 2, 3, 4),
            userSelectedStartTime.toLocalDate(),
            userSelectedStartTime.toLocalTime()
        )

        val matchesToSchedule = dataGenerator.pool1(competitionCategoryId)
        `when`(mockedScheduleRepository.getScheduleMatches(any(), any())).thenReturn(matchesToSchedule)

        val timeTable = generateTimeTable(startTime, duration, 8, 2)
            .mapIndexed{ index, it -> it.setId(index + 1) }

        `when`(mockedScheduleRepository.getTimeTable(competitionId)).thenReturn(timeTable)

        // Act
        competitionScheduler.scheduleCompetitionCategory(
            competitionId,
            competitionCategoryId,
            matchSchedulerSpec
        )

        // Record the spec sent to the repository for validation
        Mockito.verify(mockedScheduleRepository).updateMatchesTimeTablesSlots(TestHelper.MockitoHelper.capture(classCaptor))
        val result = classCaptor.value as List<MapMatchToTimeTableSlotSpec>

        // Assert
        Assertions.assertNotNull(result)
        Assertions.assertEquals(matchesToSchedule.size, result.size,
            "Not the correct number of matches scheduled.")
        Assertions.assertEquals(matchesToSchedule.size, result.map { it.matchId }.distinct().size,
            "Not all matches were scheduled")
        Assertions.assertEquals(matchesToSchedule.size, result.map { it.timeTableSlotId }.distinct().size,
            "Not unique timeslots where used")

        Assertions.assertEquals(
            listOf(5, 6, 7, 8, 9, 10),
            result.map { it.timeTableSlotId }.sorted(),
            "Expected the ${matchesToSchedule.size} matches to be scheduled on tables 1 and 2 starting from the" +
                    " 3 timeslots due to the start time filter\n" +
                    timeTable.printOut())
    }

    @Test
    fun schedulingPlayoffMatches() {
        // Setup
        val competitionId = 12
        `when`(mockedFindCompetitions.byId(any())).thenReturn(dataGenerator.newCompetitionDTO(competitionId))

        val competitionCategoryId = 123
        val duration = 25.toDuration(TimeUnit.MINUTES)
        val startTime = LocalDateTime.now()
        val matchSchedulerSpec = dataGenerator.newMatchSchedulerSpec(
            MatchType.PLAYOFF, listOf(1, 2, 3, 4),
            startTime.toLocalDate(),
            startTime.toLocalTime()
        )

        val matchesToSchedule = dataGenerator.playOff()
        `when`(mockedScheduleRepository.getScheduleMatches(any(), any())).thenReturn(matchesToSchedule)

        val timeTable = generateTimeTable(startTime, duration, 8, 4)
            .mapIndexed{ index, it -> it.setId(index + 1) }

        `when`(mockedScheduleRepository.getTimeTable(competitionId)).thenReturn(timeTable)

        // Act
        competitionScheduler.scheduleCompetitionCategory(
            competitionId,
            competitionCategoryId,
            matchSchedulerSpec
        )

        // Record the spec sent to the repository for validation
        Mockito.verify(mockedScheduleRepository).updateMatchesTimeTablesSlots(TestHelper.MockitoHelper.capture(classCaptor))
        val result = classCaptor.value as List<MapMatchToTimeTableSlotSpec>

        // Assert
        Assertions.assertNotNull(result)
        Assertions.assertEquals(matchesToSchedule.size, result.size,
            "Not the correct number of matches scheduled.")
        Assertions.assertEquals(matchesToSchedule.size, result.map { it.matchId }.distinct().size,
            "Not all matches were scheduled")
        Assertions.assertEquals(matchesToSchedule.size, result.map { it.timeTableSlotId }.distinct().size,
            "Not unique timeslots where used")

        Assertions.assertEquals(
            listOf(1, 2, 3, 4, 5, 6, 9),
            result.map { it.timeTableSlotId }.sorted(),
            "Expected the ${matchesToSchedule.size} matches to be scheduled on tables 1, 2, 3, 4." +
                    " 4 matches in first time slot, 2 matches in second, and 1 match on the third\n" +
                    timeTable.printOut())
    }

    @Test
    fun schedulingPlayoffMatchesWithVaryingNumberOfAvailableTables() {
        // Setup
        val competitionId = 12
        `when`(mockedFindCompetitions.byId(any())).thenReturn(dataGenerator.newCompetitionDTO(competitionId))

        val competitionCategoryId = 123
        val duration = 25.toDuration(TimeUnit.MINUTES)
        val startTime = LocalDateTime.now()
        val matchSchedulerSpec = dataGenerator.newMatchSchedulerSpec(
            MatchType.PLAYOFF, listOf(1, 2, 3, 4),
            startTime.toLocalDate(),
            startTime.toLocalTime()
        )

        val matchesToSchedule = dataGenerator.playOff(Round.ROUND_OF_16)
        `when`(mockedScheduleRepository.getScheduleMatches(any(), any())).thenReturn(matchesToSchedule)

        val timeTable = generateTimeTable(startTime, duration, 8, 4)
            .mapIndexed{ index, it -> it.setId(index + 1) }
            // Occupies table number 1 & 2 in the first 3 timeslots
            .map { if ((it.id % 4 == 1 || it.id % 4 == 2) && it.id < 12 ) it.Occupy() else it }

        `when`(mockedScheduleRepository.getTimeTable(competitionId)).thenReturn(timeTable)

        // Act
        competitionScheduler.scheduleCompetitionCategory(
            competitionId,
            competitionCategoryId,
            matchSchedulerSpec
        )

        // Record the spec sent to the repository for validation
        Mockito.verify(mockedScheduleRepository).updateMatchesTimeTablesSlots(TestHelper.MockitoHelper.capture(classCaptor))
        val result = classCaptor.value as List<MapMatchToTimeTableSlotSpec>

        // Assert
        Assertions.assertNotNull(result)
        Assertions.assertEquals(matchesToSchedule.size, result.size,
            "Not the correct number of matches scheduled.")
        Assertions.assertEquals(matchesToSchedule.size, result.map { it.matchId }.distinct().size,
            "Not all matches were scheduled")
        Assertions.assertEquals(matchesToSchedule.size, result.map { it.timeTableSlotId }.distinct().size,
            "Not unique timeslots where used")

        Assertions.assertEquals(
            listOf(3, 4, 7, 8, 11, 12, 13, 14, 17, 18, 19, 20, 21, 22, 25),
            result.map { it.timeTableSlotId }.sorted(),
            "Expected the ${matchesToSchedule.size} matches to be scheduled on tables 1, 2, 3, 4\n" +
                    timeTable.printOut())
    }

    private fun List<TimeTableSlotToMatch>.printOut(): String {
        val groupedByTime = this.groupBy { it.startTime }
        var printOut = "Timetable:\n"
        for (timeslot in groupedByTime) {
            printOut += timeslot.key.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) +
                    "|\t" + timeslot.value.joinToString("\t") { it.id.toString() } + "\n"
        }
        return printOut
    }

    private fun generateTimeTable(
        startTime: LocalDateTime,
        matchTime: Duration,
        numberOfTimeSlots: Int,
        numberOfTables: Int
    ): List<TimeTableSlotToMatch> {
        return (1..numberOfTimeSlots)
            .map { startTime.plusMinutes(matchTime.inWholeMinutes * it) }
            .flatMapIndexed { timeSlot, it -> rowOfTimeTableSlotToMatch(it, timeSlot, numberOfTables) }
    }

    private fun rowOfTimeTableSlotToMatch(
        startTime: LocalDateTime,
        timeSlot: Int,
        numberOfTables: Int
    ): List<TimeTableSlotToMatch> {
        return (1..numberOfTables).map { tableNumber ->
            newTimeTableSlotToMatch((timeSlot*numberOfTables) + tableNumber - 1, startTime, tableNumber)
        }
    }

    private fun newTimeTableSlotToMatch(id: Int, startTime: LocalDateTime, tableNumber: Int): TimeTableSlotToMatch {
        return TimeTableSlotToMatch(
            id,
            startTime,
            tableNumber,
            "HALL A",
            matchInfo = null)
    }

    private fun TimeTableSlotToMatch.setId(id: Int): TimeTableSlotToMatch {
        return TimeTableSlotToMatch(
            id,
            this.startTime,
            this.tableNumber,
            this.location,
            this.matchInfo
        )
    }

    private fun TimeTableSlotToMatch.Occupy(): TimeTableSlotToMatch {
        return TimeTableSlotToMatch(
            this.id,
            this.startTime,
            this.tableNumber,
            this.location,
            TimeTableSlotMatchInfo(22, 44) // Ids does not matter
        )
    }
}