package com.graphite.competitionplanner.common

import java.time.LocalDate

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
