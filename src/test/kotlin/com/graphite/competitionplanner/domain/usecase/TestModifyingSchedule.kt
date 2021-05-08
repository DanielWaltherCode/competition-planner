package com.graphite.competitionplanner.domain.usecase

import com.graphite.competitionplanner.domain.dto.ClubDTO
import com.graphite.competitionplanner.domain.dto.MatchDTO
import com.graphite.competitionplanner.domain.dto.PlayerDTO
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.lang.IllegalArgumentException
import java.time.LocalDate

@SpringBootTest
class TestModifyingSchedule(
    @Autowired val modifySchedule: ModifySchedule,
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
    fun whenModifyingNumberOfTablesThenOriginalMatchesShouldRemain() {
        val original = createSchedule.execute(pool2, 4)

        val modified = modifySchedule.execute(original, 1)

        val matchesInOriginalSchedule = original.timeslots.flatMap { it.matches }
        val matchesInModifiedSchedule = modified.timeslots.flatMap { it.matches }

        Assertions.assertEquals(matchesInOriginalSchedule, matchesInModifiedSchedule)
    }

    @Test
    fun modifyingAndCreatingAreEqual() {
        val matches = pool1 + pool2
        val schedule = createSchedule.execute(matches, 3)

        val scheduleToBeModified = createSchedule.execute(matches, 4)
        val modified = modifySchedule.execute(scheduleToBeModified, 3)

        Assertions.assertEquals(schedule, modified)
    }

    @Test
    fun whenModifyingScheduleToZeroTablesItShouldThrowIllegalArgumentException() {
        val matches = pool1 + pool3
        val schedule = createSchedule.execute(matches, 3)

        Assertions.assertThrows(IllegalArgumentException::class.java) { modifySchedule.execute(schedule, 0) }
    }

}