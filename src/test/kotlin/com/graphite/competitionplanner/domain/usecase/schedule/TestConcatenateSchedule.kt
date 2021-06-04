package com.graphite.competitionplanner.domain.usecase.schedule

import com.graphite.competitionplanner.DataGenerator
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestConcatenateSchedule(
    @Autowired val concatSchedules: ConcatSchedule,
    @Autowired val createSchedule: CreateSchedule
) {

    private final val dataGenerator = DataGenerator()
    val pool1 = dataGenerator.pool1()
    val pool2 = dataGenerator.pool2()
    val pool3 = dataGenerator.pool3()

    @Test
    fun whenConcatenatingTwoSchedulesTheTotalNumberOfTimeslotsEqualTheSumOfTimeslotsForEachSchedule() {
        val settings = dataGenerator.newScheduleSettingsDTO(numberOfTables = 4)
        val first = createSchedule.execute(pool1, settings)
        val second = createSchedule.execute(pool2, settings)

        val expectedNumberOfTimeSlots = first.timeslots.size + second.timeslots.size

        val result = concatSchedules.execute(first, second)

        Assertions.assertEquals(expectedNumberOfTimeSlots, result.timeslots.size)
    }

    @Test
    fun whenConcatenatingTwoSchedulesThereShouldNotBeAnyGapsInTheRangeOfTimeslotIds() {
        /**
         * This test check two things at once:
         * - Order is correct i.e. ranging from 0, 1, ...
         * - Timeslot IDs start at 0
         */

        val first = createSchedule.execute(pool1, dataGenerator.newScheduleSettingsDTO(numberOfTables = 4))
        val second = createSchedule.execute(pool3, dataGenerator.newScheduleSettingsDTO(numberOfTables = 3))

        val result = concatSchedules.execute(first, second)

        val thereAreNoGaps = result.timeslots.mapIndexed { index, value -> index == value.orderNumber }.all { it }

        Assertions.assertTrue(thereAreNoGaps)
    }

    @Test
    fun whenConcatenatingTwoSchedulesAllMatchesFromSecondScheduleIsPlayedAfterTheFirst() {
        val first = createSchedule.execute(pool2, dataGenerator.newScheduleSettingsDTO(numberOfTables = 4))
        val second = createSchedule.execute(pool3, dataGenerator.newScheduleSettingsDTO(numberOfTables = 4))

        val result = concatSchedules.execute(first, second)

        // Split the concatenated schedule into two parts, and extract matches.
        // Note: This is indirectly the assertion. If we concatenated correctly -> This split works
        val matchesFromFirstPart =
            result.timeslots.filterIndexed { index, _ -> index < first.timeslots.size }.flatMap { it.matches }
        val matchesFromSecondPart =
            result.timeslots.filterIndexed { index, _ -> index >= first.timeslots.size }.flatMap { it.matches }

        // Extract matches from both original schedules
        val matchesFromFirstSchedule = first.timeslots.flatMap { it.matches }
        val matchesFromSecondSchedule = second.timeslots.flatMap { it.matches }

        Assertions.assertEquals(matchesFromFirstSchedule, matchesFromFirstPart)
        Assertions.assertEquals(matchesFromSecondSchedule, matchesFromSecondPart)
    }
}