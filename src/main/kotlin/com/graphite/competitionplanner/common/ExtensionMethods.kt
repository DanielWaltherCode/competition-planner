package com.graphite.competitionplanner.common

import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.time.Duration

/**
 * Return a list of dates of the closed range
 *
 * First date in the list is the start of the range, and the last date in the list is the end of the range.
 */
fun ClosedRange<LocalDate>.toList(): List<LocalDate> {
    val result = mutableListOf<LocalDate>()

    var currentDate = this.start
    while (currentDate <= this.endInclusive) {
        result.add(currentDate)
        currentDate = currentDate.plusDays(1)
    }

    return result
}


/**
 * Adds the duration to the LocalDateTime and return a copy of it
 */
fun LocalDateTime.plusDuration(duration: Duration): LocalDateTime {
    return this.plusMinutes(duration.inWholeMinutes)
}
