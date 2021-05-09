package com.graphite.competitionplanner.domain.usecase

import com.graphite.competitionplanner.domain.dto.ClubDTO
import com.graphite.competitionplanner.domain.dto.MatchDTO
import com.graphite.competitionplanner.domain.dto.PlayerDTO
import com.graphite.competitionplanner.domain.dto.ScheduleMetaDataDTO
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDate

@SpringBootTest
class TestConcatenateSchedule(
    @Autowired val concatSchedules: ConcatSchedule,
    @Autowired val createSchedule: CreateSchedule
) {

    /**
     * A pool of 4 players always have 2 independent matches that can be played simultaneously
     * A pool of 3 players only has one independent match that can be played at any time
     */
    private final val birthDate = LocalDate.of(1999, 4, 3)
    private final val club = ClubDTO(33, "Luleå", "Midsommarvägen 13")
    private final val p1 = PlayerDTO(1, "Jan", "Olsson", club, birthDate)
    private final val p2 = PlayerDTO(2, "Gill", "Fiskarsson", club, birthDate)
    private final val p3 = PlayerDTO(3, "Sven", "Svensson", club, birthDate)
    private final val p4 = PlayerDTO(4, "Sture", "Sundberg", club, birthDate)

    val pool1 = listOf(
        MatchDTO(
            1, null, null, 1, "POOL",
            listOf(p1), listOf(p2), 0, "GROUP"
        ),
        MatchDTO(
            2, null, null, 1, "POOL",
            listOf(p1), listOf(p3), 0, "GROUP"
        ),
        MatchDTO(
            3, null, null, 1, "POOL",
            listOf(p1), listOf(p4), 0, "GROUP"
        ),
        MatchDTO(
            4, null, null, 1, "POOL",
            listOf(p2), listOf(p3), 0, "GROUP"
        ),
        MatchDTO(
            5, null, null, 1, "POOL",
            listOf(p2), listOf(p4), 0, "GROUP"
        ),
        MatchDTO(
            6, null, null, 1, "POOL",
            listOf(p3), listOf(p4), 0, "GROUP"
        )
    )
    private final val p5 = PlayerDTO(5, "Elin", "Malsson", club, birthDate)
    private final val p6 = PlayerDTO(6, "Ewa", "Svensson", club, birthDate)
    private final val p7 = PlayerDTO(7, "Katarina", "Dalhborg", club, birthDate)
    private final val p8 = PlayerDTO(8, "Lena", "Sinè", club, birthDate)

    val pool2 = listOf(
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
            listOf(p5), listOf(p8), 0, "GROUP"
        ),
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

    private final val p9 = PlayerDTO(9, "Patrik", "Larsson", club, birthDate)
    private final val p10 = PlayerDTO(10, "Enok", "Karlsson", club, birthDate)
    private final val p11 = PlayerDTO(11, "Tintin", "Snäll", club, birthDate)

    val pool3 = listOf(
        MatchDTO(
            13, null, null, 1, "POOL",
            listOf(p9), listOf(p10), 0, "GROUP"
        ),
        MatchDTO(
            14, null, null, 1, "POOL",
            listOf(p9), listOf(p11), 0, "GROUP"
        ),
        MatchDTO(
            15, null, null, 1, "POOL",
            listOf(p10), listOf(p11), 0, "GROUP"
        )
    )


    @Test
    fun whenConcatenatingTwoSchedulesTheTotalNumberOfTimeslotsEqualTheSumOfTimeslotsForEachSchedule() {
        val first = createSchedule.execute(pool1, ScheduleMetaDataDTO(15, 4))
        val second = createSchedule.execute(pool2, ScheduleMetaDataDTO(15, 4))

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

        val first = createSchedule.execute(pool1, ScheduleMetaDataDTO(15, 4))
        val second = createSchedule.execute(pool3, ScheduleMetaDataDTO(15, 3))

        val result = concatSchedules.execute(first, second)

        val thereAreNoGaps = result.timeslots.mapIndexed { index, value -> index == value.orderNumber }.all { it }

        Assertions.assertTrue(thereAreNoGaps)
    }

    @Test
    fun whenConcatenatingTwoSchedulesAllMatchesFromSecondScheduleIsPlayedAfterTheFirst() {
        val first = createSchedule.execute(pool2, ScheduleMetaDataDTO(15, 4))
        val second = createSchedule.execute(pool3, ScheduleMetaDataDTO(15, 4))

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