package com.graphite.competitionplanner.schedule.domain

import com.graphite.competitionplanner.schedule.interfaces.IScheduleRepository
import com.graphite.competitionplanner.util.plusDuration
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.time.Duration

@Component
class GenerateTimeTable(
    val repository: IScheduleRepository
) {

    private val nine = LocalTime.of(9, 0, 0)
    private val seventeen = LocalTime.of(17, 0, 0)

    fun execute(competitionId: Int, numberOfTables: Int, estimatedMatchTime: Duration, location: String, dates: List<LocalDate>) {

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

data class TimeTableSlotSpec(
    /**
     * Start time of the time slot
     */
    val startTime: LocalDateTime,
    /**
     * Table number
     */
    val tableNumber: Int,
    /**
     * Location e.g. Arena A or Arena B. It is user defined
     */
    val location: String
)