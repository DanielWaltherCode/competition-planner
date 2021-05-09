package com.graphite.competitionplanner.domain.entity

import com.graphite.competitionplanner.domain.dto.ScheduleSettingsDTO

internal data class ScheduleSettings(
    val averageMatchTime: Int,
    val numberOfTables: Int
) {
    init {
        require(averageMatchTime > 0)
        require(numberOfTables > 0)
    }

    constructor(dto: ScheduleSettingsDTO) : this(dto.averageMatchTime, dto.numberOfTables)
}
