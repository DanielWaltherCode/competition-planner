package com.graphite.competitionplanner.domain.entity

import com.graphite.competitionplanner.domain.dto.ScheduleMetaDataDTO

internal data class ScheduleMetaData(
    val averageMatchTime: Int,
    val numberOfTables: Int
) {
    init {
        require(averageMatchTime > 0)
        require(numberOfTables > 0)
    }

    constructor(dto: ScheduleMetaDataDTO) : this(dto.averageMatchTime, dto.numberOfTables)
}
