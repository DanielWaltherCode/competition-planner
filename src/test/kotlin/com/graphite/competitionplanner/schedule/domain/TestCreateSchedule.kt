package com.graphite.competitionplanner.schedule.domain

import com.graphite.competitionplanner.util.DataGenerator
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestCreateSchedule(@Autowired val createSchedule: CreateSchedule) {

    /**
     * A pool of 4 players always have 2 independent matches that can be played simultaneously
     * A pool of 3 players only has one independent match that can be played at any time
     */
    private final val dataGenerator = DataGenerator()
    private final val pool1 = dataGenerator.pool1()
    private final val pool2 = dataGenerator.pool2()
    private final val pool3 = dataGenerator.pool3()

    @Test
    fun oneTableForAllMatches() {

        val matches = pool1 + pool2

        val schedule = createSchedule.execute(matches, dataGenerator.newScheduleSettingsDTO(numberOfTables = 1))

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
        val schedule = createSchedule.execute(matches, dataGenerator.newScheduleSettingsDTO(numberOfTables = 2))

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
        val schedule = createSchedule.execute(matches, dataGenerator.newScheduleSettingsDTO(numberOfTables = 3))

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
        val schedule = createSchedule.execute(matches, dataGenerator.newScheduleSettingsDTO(numberOfTables = 2))

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
        val schedule = createSchedule.execute(matches, dataGenerator.newScheduleSettingsDTO(numberOfTables = 3))

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
        val schedule = createSchedule.execute(matches, dataGenerator.newScheduleSettingsDTO(numberOfTables = 4))

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
        val schedule = createSchedule.execute(matches, dataGenerator.newScheduleSettingsDTO(numberOfTables = 4))

        Assertions.assertEquals(3, schedule.timeslots.size)
    }

    @Test
    fun shouldNotScheduleAPlayerTwiceInSameTimeslot() {
        val matches = pool1
        val schedule = createSchedule.execute(matches, dataGenerator.newScheduleSettingsDTO(numberOfTables = 4))

        for (timeslot in schedule.timeslots) {
            val playerIds = timeslot.matches.flatMap { it.firstTeamPlayerIds + it.secondTeamPlayerIds }
            Assertions.assertEquals(playerIds.size, playerIds.distinct().size)
        }
    }

    @Test
    fun balanceMatchesWithTwoCategories() {
        /**
         * Given two categories:
         * - Where one category is larger than the other,
         * - and we want both categories to start playing their matches at the same time.
         * We want the two categories to split the number of available tables evenly.
         *
         * In this case we have two categories
         * - Category 1 can play 6 parallel matches
         * - Category 2 can play 4 parallel matches
         * - We are bound to 8 tables
         *
         * Current implementation performs scheduling in a round-robin fashion which means we would expect four
         * matches from each category be scheduled per timeslot (until the smaller class runs out of matches)
         */

        val classOnePoolA = dataGenerator.poolOf(numberOfPlayers = 4, categoryId = 1)
        val classOnePoolB = dataGenerator.poolOf(numberOfPlayers = 4, categoryId = 1)
        val classOnePoolC = dataGenerator.poolOf(numberOfPlayers = 4, categoryId = 1)

        val classTwoPoolA = dataGenerator.poolOf(numberOfPlayers = 4, categoryId = 2)
        val classTwoPoolB = dataGenerator.poolOf(numberOfPlayers = 4, categoryId = 2)

        val allMatches = classOnePoolA + classOnePoolB + classOnePoolC + classTwoPoolA + classTwoPoolB

        val settings = dataGenerator.newScheduleSettingsDTO(numberOfTables = 8)

        val schedule = createSchedule.execute(allMatches, settings)

        val matchesFromCategoryOne = schedule.timeslots.first().matches.filter { it.competitionCategoryId == 1 }
        val matchesFromCategoryTwo = schedule.timeslots.first().matches.filter { it.competitionCategoryId == 2 }

        Assertions.assertTrue(matchesFromCategoryOne.size >= 4, "Expected category one to get at least 4 tables")
        Assertions.assertTrue(matchesFromCategoryTwo.size >= 4, "Expected category two to get at least 4 tables")
    }

    @Test
    fun balanceMatchesWithThreeCategories() {
        /**
         * Due to the round-robin scheduling we can expect that each category gets at least two matches per timeslot given
         * we have 8 tables divided on 3 categories.
         */
        val classOnePoolA = dataGenerator.poolOf(numberOfPlayers = 4, categoryId = 1)
        val classOnePoolB = dataGenerator.poolOf(numberOfPlayers = 4, categoryId = 1)
        val classOnePoolC = dataGenerator.poolOf(numberOfPlayers = 4, categoryId = 1)

        val classTwoPoolA = dataGenerator.poolOf(numberOfPlayers = 4, categoryId = 2)
        val classTwoPoolB = dataGenerator.poolOf(numberOfPlayers = 4, categoryId = 2)

        val classThreePoolA = dataGenerator.poolOf(numberOfPlayers = 4, categoryId = 3)
        val classThreePoolB = dataGenerator.poolOf(numberOfPlayers = 4, categoryId = 3)

        val allMatches =
            classOnePoolA + classOnePoolB + classOnePoolC + classTwoPoolA + classTwoPoolB + classThreePoolA + classThreePoolB

        val settings = dataGenerator.newScheduleSettingsDTO(numberOfTables = 8)

        val schedule = createSchedule.execute(allMatches, settings)

        val matchesFromCategoryOne = schedule.timeslots.first().matches.filter { it.competitionCategoryId == 1 }
        val matchesFromCategoryTwo = schedule.timeslots.first().matches.filter { it.competitionCategoryId == 2 }
        val matchesFromCategoryThree = schedule.timeslots.first().matches.filter { it.competitionCategoryId == 3 }

        Assertions.assertTrue(matchesFromCategoryOne.size >= 2)
        Assertions.assertTrue(matchesFromCategoryTwo.size >= 2)
        Assertions.assertTrue(matchesFromCategoryThree.size >= 2)
    }

    @Test
    fun balanceMatchesWhenFewTables() {
        val classOnePoolA = dataGenerator.poolOf(numberOfPlayers = 4, categoryId = 1)
        val classOnePoolB = dataGenerator.poolOf(numberOfPlayers = 4, categoryId = 1)
        val classOnePoolC = dataGenerator.poolOf(numberOfPlayers = 4, categoryId = 1)

        val classTwoPoolA = dataGenerator.poolOf(numberOfPlayers = 4, categoryId = 2)

        val allMatches = classOnePoolA + classOnePoolB + classOnePoolC + classTwoPoolA

        val settings = dataGenerator.newScheduleSettingsDTO(numberOfTables = 2)

        val schedule = createSchedule.execute(allMatches, settings)

        val matchesFromCategoryOne = schedule.timeslots.first().matches.filter { it.competitionCategoryId == 1 }
        val matchesFromCategoryTwo = schedule.timeslots.first().matches.filter { it.competitionCategoryId == 2 }

        Assertions.assertTrue(matchesFromCategoryOne.isNotEmpty())
        Assertions.assertTrue(matchesFromCategoryTwo.isNotEmpty())
    }

    @Test
    fun shouldThrowIllegalArgumentExceptionWhenNumberOfTablesIsZero() {
        val matches = pool1 + pool3
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            createSchedule.execute(
                matches,
                dataGenerator.newScheduleSettingsDTO(numberOfTables = 0)
            )
        }
    }
}