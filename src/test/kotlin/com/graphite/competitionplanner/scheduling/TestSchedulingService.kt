package com.graphite.competitionplanner.scheduling

import com.graphite.competitionplanner.api.ClubNoAddressDTO
import com.graphite.competitionplanner.service.MatchDTO
import com.graphite.competitionplanner.service.PlayerDTO
import com.graphite.competitionplanner.service.SchedulingService
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.lang.IllegalArgumentException
import java.time.LocalDate

@SpringBootTest
class TestSchedulingService (@Autowired val schedulingService: SchedulingService){


    /**
     * A pool of 4 players always have 2 independent matches that can be played simultaneously
     * A pool of 3 players only has one independent match that can be played at any time
     */
    private final val p1 = PlayerDTO(1, "Jan", "Olsson", ClubNoAddressDTO(1, "Luleå"), LocalDate.now())
    private final val p2 = PlayerDTO(2, "Gill", "Fiskarsson", ClubNoAddressDTO(1, "Luleå"), LocalDate.now())
    private final val p3 = PlayerDTO(3, "Sven", "Svensson", ClubNoAddressDTO(1, "Luleå"), LocalDate.now())
    private final val p4 = PlayerDTO(4, "Sture", "Sundberg", ClubNoAddressDTO(1, "Luleå"), LocalDate.now())

    val pool1 = listOf(
            MatchDTO(1, null, null, 1, "whatever",
                    listOf(p1), listOf(p2), 0, "GROUP"),
            MatchDTO(2, null, null, 1, "whatever",
                    listOf(p1), listOf(p3), 0, "GROUP"),
            MatchDTO(3, null, null, 1, "whatever",
                    listOf(p1), listOf(p4), 0, "GROUP"),
            MatchDTO(4, null, null, 1, "whatever",
                    listOf(p2), listOf(p3), 0, "GROUP"),
            MatchDTO(5, null, null, 1, "whatever",
                    listOf(p2), listOf(p4), 0, "GROUP"),
            MatchDTO(6, null, null, 1, "whatever",
                    listOf(p3), listOf(p4), 0, "GROUP")
    )
    private final val p5 = PlayerDTO(5, "Elin", "Malsson", ClubNoAddressDTO(1, "Luleå"), LocalDate.now())
    private final val p6 = PlayerDTO(6, "Ewa", "Svensson", ClubNoAddressDTO(1, "Luleå"), LocalDate.now())
    private final val p7 = PlayerDTO(7, "Katarina", "Dalhborg", ClubNoAddressDTO(1, "Luleå"), LocalDate.now())
    private final val p8 = PlayerDTO(8, "Lena", "Sinè", ClubNoAddressDTO(1, "Luleå"), LocalDate.now())

    val pool2 = listOf(
            MatchDTO(7, null, null, 1, "whatever",
                    listOf(p5), listOf(p6), 0, "GROUP"),
            MatchDTO(8, null, null, 1, "whatever",
                    listOf(p5), listOf(p7), 0, "GROUP"),
            MatchDTO(9, null, null, 1, "whatever",
                    listOf(p5), listOf(p8), 0, "GROUP"),
            MatchDTO(10, null, null, 1, "whatever",
                    listOf(p6), listOf(p7), 0, "GROUP"),
            MatchDTO(11, null, null, 1, "whatever",
                    listOf(p6), listOf(p8), 0, "GROUP"),
            MatchDTO(12, null, null, 1, "whatever",
                    listOf(p7), listOf(p8), 0, "GROUP")
    )

    private final val p9 = PlayerDTO(9, "Patrik", "Larsson", ClubNoAddressDTO(1, "Luleå"), LocalDate.now())
    private final val p10 = PlayerDTO(10, "Enok", "Karlsson", ClubNoAddressDTO(1, "Luleå"), LocalDate.now())
    private final val p11 = PlayerDTO(11, "Tintin", "Snäll", ClubNoAddressDTO(1, "Luleå"), LocalDate.now())

    val pool3 = listOf(
            MatchDTO(13, null, null, 1, "whatever",
                    listOf(p9), listOf(p10), 0, "GROUP"),
            MatchDTO(14, null, null, 1, "whatever",
                    listOf(p9), listOf(p11), 0, "GROUP"),
            MatchDTO(15, null, null, 1, "whatever",
                    listOf(p10), listOf(p11), 0, "GROUP")
    )

    @Test
    fun oneTableForAllMatches(){

        val matches = pool1 + pool2
        val schedule = schedulingService.create(matches, 1)

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
        val schedule = schedulingService.create(matches, 2)

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
        val schedule = schedulingService.create(matches, 3)

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
        val schedule = schedulingService.create(matches, 2)

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
        val schedule = schedulingService.create(matches, 3)

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
        val schedule = schedulingService.create(matches, 4)

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
        val schedule = schedulingService.create(matches, 4)

        Assertions.assertEquals(3, schedule.timeslots.size)
    }

    @Test
    fun shouldNotScheduleAPlayerTwiceInSameTimeslot() {
        val matches = pool1
        val schedule = schedulingService.create(matches, 4)

        for (timeslot in schedule.timeslots) {
            Assertions.assertEquals(timeslot.playerIds.size, timeslot.playerIds.distinct().size)
        }
    }

    @Test
    fun shouldThrowIllegalArgumentExceptionWhenNumberOfTablesIsZero(){
        val matches = pool1 + pool3

        Assertions.assertThrows(IllegalArgumentException::class.java) { schedulingService.create(matches, 0) }
    }

    @Test
    fun shouldThrowIllegalArgumentExceptionWhenThereIsAnInvalidMatch(){
        val matches = listOf(
                MatchDTO(7, null, null, 1, "whatever",
                        listOf(p5), listOf(p6), 0, "GROUP"),
                MatchDTO(8, null, null, 1, "whatever",
                        listOf(p5), listOf(p7), 0, "GROUP"),
                MatchDTO(9, null, null, 1, "whatever",
                        listOf(p5), listOf(p5), 0, "GROUP"), // Illegal
                MatchDTO(10, null, null, 1, "whatever",
                        listOf(p6), listOf(p7), 0, "GROUP"),
                MatchDTO(11, null, null, 1, "whatever",
                        listOf(p6), listOf(p8), 0, "GROUP"),
                MatchDTO(12, null, null, 1, "whatever",
                        listOf(p7), listOf(p8), 0, "GROUP")
        )

        Assertions.assertThrows(IllegalArgumentException::class.java) { schedulingService.create(matches, 1) }
    }

    @Test
    fun whenConcatenatingTwoSchedulesTheTotalNumberOfTimeslotsEqualTheSumOfTimeslotsForEachSchedule(){
        val first = schedulingService.create(pool1, 4)
        val second = schedulingService.create(pool2, 4)

        val expectedNumberOfTimeSlots = first.timeslots.size + second.timeslots.size

        val result = schedulingService.concat(first, second)

        Assertions.assertEquals(expectedNumberOfTimeSlots, result.timeslots.size)
    }

    @Test
    fun whenConcatenatingTwoSchedulesThereShouldNotBeAnyGapsInTheRangeOfTimeslotIds(){
        /**
         * This test check two things at once:
         * - Order is correct i.e. ranging from 0, 1, ...
         * - Timeslot IDs start at 0
         */

        val first = schedulingService.create(pool1, 4)
        val second = schedulingService.create(pool3, 3)

        val result = schedulingService.concat(first, second)

        val thereAreNoGaps = result.timeslots.mapIndexed{ index, value -> index == value.id }.all { it }

        Assertions.assertTrue(thereAreNoGaps)
    }

    @Test
    fun whenConcatenatingTwoSchedulesAllMatchesFromSecondScheduleIsPlayedAfterTheFirst(){
        val first = schedulingService.create(pool2, 4)
        val second = schedulingService.create(pool3, 4)

        val result = schedulingService.concat(first, second)

        // Split the concatenated schedule into two parts, and extract matches.
        // Note: This is indirectly the assertion. If we concatenated correctly -> This split works
        val matchesFromFirstPart = result.timeslots.filterIndexed{ index, _ -> index < first.timeslots.size}.flatMap { it.matches }
        val matchesFromSecondPart = result.timeslots.filterIndexed{ index, _ -> index >= first.timeslots.size}.flatMap { it.matches }

        // Extract matches from both original schedules
        val matchesFromFirstSchedule = first.timeslots.flatMap { it.matches }
        val matchesFromSecondSchedule = second.timeslots.flatMap { it.matches }

        Assertions.assertEquals(matchesFromFirstSchedule, matchesFromFirstPart)
        Assertions.assertEquals(matchesFromSecondSchedule, matchesFromSecondPart)
    }
}
