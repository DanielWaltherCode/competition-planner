package com.graphite.competitionplanner.domain.dto

import com.graphite.competitionplanner.domain.entity.ScheduleMetaData

data class ScheduleMetaDataDTO(
    val averageMatchTime: Int,
    val numberOfTables: Int
) {
    internal constructor(scheduleMetaData: ScheduleMetaData) : this(
        scheduleMetaData.averageMatchTime,
        scheduleMetaData.numberOfTables
    )
}
