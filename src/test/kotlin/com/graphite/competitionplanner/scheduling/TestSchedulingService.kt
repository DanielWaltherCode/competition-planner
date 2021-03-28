package com.graphite.competitionplanner.scheduling

import com.graphite.competitionplanner.service.SchedulingService
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.util.Assert
import java.lang.IllegalArgumentException

@SpringBootTest
class TestSchedulingService (@Autowired val schedulingService: SchedulingService){

    /**
     * A pool of 4 players always have 2 independent matches that can be played simultaneously
     * A pool of 3 players only has one independent match that can be played at any time
     */
    val pool1 = listOf(
            SchedulingService.TempMatch(listOf(1, 2)),
            SchedulingService.TempMatch(listOf(1, 3)),
            SchedulingService.TempMatch(listOf(1, 4)),
            SchedulingService.TempMatch(listOf(2, 3)),
            SchedulingService.TempMatch(listOf(2, 4)),
            SchedulingService.TempMatch(listOf(3, 4))
    )

    val pool2 = listOf(
            SchedulingService.TempMatch(listOf(5, 6)),
            SchedulingService.TempMatch(listOf(5, 7)),
            SchedulingService.TempMatch(listOf(5, 8)),
            SchedulingService.TempMatch(listOf(6, 7)),
            SchedulingService.TempMatch(listOf(6, 8)),
            SchedulingService.TempMatch(listOf(7, 8))
    )

    val pool3 = listOf(
            SchedulingService.TempMatch(listOf(9, 10)),
            SchedulingService.TempMatch(listOf(9, 11)),
            SchedulingService.TempMatch(listOf(10, 11))
    )


    @Test
    fun oneTableForAllMatches(){

        val matches = pool1 + pool2
        val schedule = schedulingService.createSchedule(matches, 1)

        Assertions.assertEquals(matches.size, schedule.timeslots.size)
    }

    @Test
    fun twoTablesForAllMatches(){

        /**
         *  Example of optimal schedule with pool of 4 players
         *    T1     |   T2   |   T3
         *    (1-2)    (1-3)     (1-4)
         *    (3-4)    (2-4)     (3-4)
         */
        val matches = pool1
        val schedule = schedulingService.createSchedule(matches, 2)

        Assertions.assertEquals(3, schedule.timeslots.size)
    }

    @Test
    fun moreTablesThanNecessaryShouldNotAssignPlayerToSameTimeslotTwice(){
        /**
         *  Example of optimal schedule with pool of 4 players, even if we have more tables per timeslot available
         *    T1     |   T2   |   T3
         *    (1-2)    (1-3)     (1-4)
         *    (3-4)    (2-4)     (3-4)
         *    (empty)  (empty)   (empty)
         */
        val matches = pool1
        val schedule = schedulingService.createSchedule(matches, 3)

        Assertions.assertEquals(3, schedule.timeslots.size)
    }

    @Test
    fun scheduleForTwoPoolsTwoTables(){

        /**
         * This case we have 4 independent matches that we can choose from. But we are bound to 2 tables.
         *
         * Optimal solution should have 6 timeslots
         */

        val matches = pool1 + pool2
        val schedule = schedulingService.createSchedule(matches, 2)

        Assertions.assertEquals(matches.size / 2, schedule.timeslots.size)
    }

    @Test
    fun scheduleForTwoPoolsThreeTables(){

        /**
         * This case we have 4 independent matches that can choose from. But we are bound to 3 tables.
         *
         * Example of optimal solution:
         *
         *   T1     |   T2   |   T3   |  T4
         *   (1-2)     (7-8)    (5-7)    (2-3)
         *   (3-4)     (1-3)    (6-8)    (5-8)
         *   (5-6)     (2-4)    (1-4)    (6-7)
         */
        val matches = pool1 + pool2
        val schedule = schedulingService.createSchedule(matches, 3)

        Assertions.assertEquals(4, schedule.timeslots.size)
    }

    @Test
    fun scheduleForTwoPoolsFourTables(){

        /**
         * This case we have 4 independent matches that we can choose from. We are bound to 4 tables.
         *
         * Example of optimal solution:
         *    T1     |   T2   |   T3   |
         *    (1-2)     (1-3)    (1-4)
         *    (3-4)     (2-4)    (2-3)
         *    (5-6)     (5-7)    (5-8)
         *    (7-8)     (6-8)    (7-8)
         */

        val matches = pool1 + pool2
        val schedule = schedulingService.createSchedule(matches, 4)

        Assertions.assertEquals(3, schedule.timeslots.size)
    }

    @Test
    fun scheduleForOneThreePoolAndOneFourPool(){

        /**
         * This case we have 3 independent matches that we can choose from. We are bound to 4 tables
         *
         * Example of optimal solution:
         *    T1     |   T2   |   T3   |
         *    (1-2)     (1-3)    (1-4)
         *    (3-4)     (2-4)    (2-3)
         *    (9-10)    (9-11)   (10-11)
         *    (empty)  (empty)   (empty)
         */

        val matches = pool1 + pool3
        val schedule = schedulingService.createSchedule(matches, 4)

        Assertions.assertEquals(3, schedule.timeslots.size)
    }

    @Test
    fun shouldThrowIllegalArgumentExceptionWhenNumberOfTablesIsZero(){
        val matches = pool1 + pool3

        Assertions.assertThrows(IllegalArgumentException::class.java) { schedulingService.createSchedule(matches, 0) }
    }

    @Test
    fun shouldThrowIllegalArgumentExceptionWhenThereIsAnInvalidMatch(){
        val matches = listOf(
                SchedulingService.TempMatch(listOf(1, 2)),
                SchedulingService.TempMatch(listOf(1, 3)),
                SchedulingService.TempMatch(listOf(1, 4)),
                SchedulingService.TempMatch(listOf(2, 2)), // Illegal
                SchedulingService.TempMatch(listOf(2, 4)),
                SchedulingService.TempMatch(listOf(3, 4))
        )

        Assertions.assertThrows(IllegalArgumentException::class.java) { schedulingService.createSchedule(matches, 1) }
    }
}