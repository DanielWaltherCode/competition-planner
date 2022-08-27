package com.graphite.competitionplanner.schedule.interfaces

data class ScheduleSettingsDTO(
    val numberOfTables: Int,
) {
    init {
        require(numberOfTables > 0) { "Number of tables must be greater than zero" }
    }
}
