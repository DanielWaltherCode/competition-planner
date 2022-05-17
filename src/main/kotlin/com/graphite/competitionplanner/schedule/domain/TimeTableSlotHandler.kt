package com.graphite.competitionplanner.schedule.domain

import com.graphite.competitionplanner.schedule.interfaces.IScheduleRepository
import com.graphite.competitionplanner.schedule.interfaces.TimeTableSlotDto
import com.graphite.competitionplanner.schedule.interfaces.TimeTableSlotSpec
import com.graphite.competitionplanner.util.plusDuration
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.time.Duration


/**
 * This is the class that handles all the modifications to the TimeTableSlots. Operations that alter these slots can be
 * - Changing play date
 * - Changing average time per game
 * - Adding lunch breaks
 * - Changing number of tables available
 * - Changing locations
 * - etc.
 *
 * Any operations to the TimeTableSlots will override any unpublished schema.
 */
@Component
class TimeTableSlotHandler(
    val repository: IScheduleRepository
) {

    // TODO: Make this more configurable
    fun init(competitionId: Int, numberOfTables: Int, estimatedMatchTime: Duration, location: String, dates: List<LocalDate>) {
        // TODO: Make input parameters
        val nine = LocalTime.of(9, 0, 0)
        val seventeen = LocalTime.of(17, 0, 0)

        val timeSlots = dates.flatMap { date: LocalDate ->
            generateStartTimes(
                LocalDateTime.of(date, nine),
                LocalDateTime.of(date, seventeen),
                estimatedMatchTime
            ).flatMap { starTime ->
                (1..numberOfTables).map { tableNumber ->
                    TimeTableSlotSpec(
                        starTime,
                        tableNumber,
                        location
                    )
                }
            }
        }

        repository.storeTimeTable(competitionId, timeSlots)

    }

    private fun generateStartTimes(
        startTime: LocalDateTime,
        endTime: LocalDateTime,
        estimatedMatchTime: Duration
    ): List<LocalDateTime> {
        val startTimes = mutableListOf<LocalDateTime>()
        var currentTime = startTime
        while (currentTime < endTime) {
            startTimes.add(currentTime)
            currentTime = currentTime.plusDuration(estimatedMatchTime)
        }
        return startTimes
    }
}

