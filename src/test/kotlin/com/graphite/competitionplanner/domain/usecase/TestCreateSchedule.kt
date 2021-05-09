package com.graphite.competitionplanner.domain.usecase

import com.graphite.competitionplanner.domain.DataGenerator
import com.graphite.competitionplanner.domain.dto.MatchDTO
import com.graphite.competitionplanner.domain.dto.ScheduleSettingsDTO
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.lang.IllegalArgumentException

@SpringBootTest
class TestCreateSchedule(@Autowired val createSchedule: CreateSchedule) {

    /**
     * A pool of 4 players always have 2 independent matches that can be played simultaneously
     * A pool of 3 players only has one independent match that can be played at any time
     */
    private final val dataGenerator = DataGenerator()
    val pool1 = dataGenerator.pool1()
    val pool2 = dataGenerator.pool2()
    val pool3 = dataGenerator.pool3()

    @Test
    fun oneTableForAllMatches() {

        val matches = pool1 + pool2

        val schedule = createSchedule.execute(matches, ScheduleSettingsDTO(15, 1))

        Assertions.assertEquals(matches.size, schedule.timeslots.size)
    }

    @Test
    fun twoTablesForAllMatches() {

        /**
         *  Example of optimal schedule with pool of 4 players
         *    T1     |   T2   |   T3
         *    (1-2)    (1-3)     (1-4)
         *    (3-4)    (2-4)     (3-4)
         */
        val matches = pool1
        val schedule = createSchedule.execute(matches, ScheduleSettingsDTO(15, 2))

        Assertions.assertEquals(3, schedule.timeslots.size)
    }

    @Test
    fun moreTablesThanNecessaryShouldNotAssignPlayerToSameTimeslotTwice() {
        /**
         *  Example of optimal schedule with pool of 4 players, even if we have more tables per timeslot available
         *    T1     |   T2   |   T3
         *    (1-2)    (1-3)     (1-4)
         *    (3-4)    (2-4)     (3-4)
         *    (empty)  (empty)   (empty)
         */
        val matches = pool1
        val schedule = createSchedule.execute(matches, ScheduleSettingsDTO(15, 3))

        Assertions.assertEquals(3, schedule.timeslots.size)
    }

    @Test
    fun scheduleForTwoPoolsTwoTables() {

        /**
         * This case we have 4 independent matches that we can choose from. But we are bound to 2 tables.
         *
         * Optimal solution should have 6 timeslots
         */

        val matches = pool1 + pool2
        val schedule = createSchedule.execute(matches, ScheduleSettingsDTO(15, 2))

        Assertions.assertEquals(matches.size / 2, schedule.timeslots.size)
    }

    @Test
    fun scheduleForTwoPoolsThreeTables() {

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
        val schedule = createSchedule.execute(matches, ScheduleSettingsDTO(15, 3))

        Assertions.assertEquals(4, schedule.timeslots.size)
    }

    @Test
    fun scheduleForTwoPoolsFourTables() {

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
        val schedule = createSchedule.execute(matches, ScheduleSettingsDTO(15, 4))

        Assertions.assertEquals(3, schedule.timeslots.size)
    }

    @Test
    fun scheduleForOneThreePoolAndOneFourPool() {

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
        val schedule = createSchedule.execute(matches, ScheduleSettingsDTO(15, 4))

        Assertions.assertEquals(3, schedule.timeslots.size)
    }

    @Test
    fun shouldNotScheduleAPlayerTwiceInSameTimeslot() {
        val matches = pool1
        val schedule = createSchedule.execute(matches, ScheduleSettingsDTO(15, 4))

        for (timeslot in schedule.timeslots) {
            val playerIds =
                timeslot.matches.flatMap { it.firstPlayer.map { p -> p.id } + it.secondPlayer.map { p -> p.id } }
            Assertions.assertEquals(playerIds.size, playerIds.distinct().size)
        }
    }

    @Test
    fun shouldThrowIllegalArgumentExceptionWhenNumberOfTablesIsZero() {
        val matches = pool1 + pool3
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            createSchedule.execute(
                matches,
                ScheduleSettingsDTO(15, 0)
            )
        }
    }

    @Test
    fun shouldThrowIllegalArgumentExceptionWhenThereIsAnInvalidMatch() {
        val p5 = dataGenerator.newPlayerDTO()
        val p6 = dataGenerator.newPlayerDTO()
        val p7 = dataGenerator.newPlayerDTO()
        val p8 = dataGenerator.newPlayerDTO()
        val matches = listOf(
            MatchDTO(
                7, null, null, 1, "POOL",
                listOf(p5), listOf(p6), 0, "GROUP"
            ),
            MatchDTO(
                8, null, null, 1, "POOL",
                listOf(p5), listOf(p7), 0, "GROUP"
            ),
            MatchDTO(
                9, null, null, 1, "POOL",
                listOf(p5), listOf(p5), 0, "GROUP"
            ), // Illegal
            MatchDTO(
                10, null, null, 1, "POOL",
                listOf(p6), listOf(p7), 0, "GROUP"
            ),
            MatchDTO(
                11, null, null, 1, "POOL",
                listOf(p6), listOf(p8), 0, "GROUP"
            ),
            MatchDTO(
                12, null, null, 1, "POOL",
                listOf(p7), listOf(p8), 0, "GROUP"
            )
        )

        Assertions.assertThrows(IllegalArgumentException::class.java) {
            createSchedule.execute(
                matches,
                ScheduleSettingsDTO(15, 3)
            )
        }
    }
}