package com.graphite.competitionplanner.schedule.api

import com.graphite.competitionplanner.schedule.repository.ScheduleRepository
import com.graphite.competitionplanner.schedule.service.AvailableTablesDTO
import com.graphite.competitionplanner.schedule.service.AvailableTablesDayDTO
import com.graphite.competitionplanner.schedule.service.AvailableTablesService
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import java.time.LocalTime

@RestController
@RequestMapping("/schedule/{competitionId}/available-tables")
class AvailableTablesApi(
    val availableTablesService: AvailableTablesService,
    val scheduleRepository: ScheduleRepository
) {

    @GetMapping("/{day}")
    fun getTablesAvailableByDay(
        @PathVariable competitionId: Int,
        @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") day: LocalDate
    ): AvailableTablesDTO {
        return availableTablesService.getTablesAvailableByDay(competitionId, day)
    }

    /**
     * Used in selection table on website. Should return either one number of available tables
     * per day, if it's the same for all hourly time slots, or return -1 if the nr of tables differs.
     * Then the number can no longer be changed in the simple table.
     */
    @GetMapping("/main-table")
    fun getTablesAvailableForMainTable(
        @PathVariable competitionId: Int
    ): List<AvailableTablesDayDTO> {
        return availableTablesService.getTablesAvailableForMainTable(competitionId)
    }

    @PostMapping
    fun registerTablesAvailable(
        @PathVariable competitionId: Int,
        @RequestBody availableTablesSpec: AvailableTablesSpec
    ): AvailableTablesDTO {
        return availableTablesService.registerTablesAvailable(competitionId, availableTablesSpec)
    }

    @PutMapping()
    fun updateTablesAvailableForAllDays(
            @PathVariable competitionId: Int,
            @RequestBody availableTablesAllDaysSpec: AvailableTablesAllDaysSpec
    ) {
        availableTablesService.updateTablesAvailable(competitionId, availableTablesAllDaysSpec)
    }

}

// Set all time slots for time to same number of tables
data class AvailableTablesWholeCompetitionSpec(
    val nrTables: Int
)

data class AvailableTablesAllDaysSpec(
        val tableDays: List<AvailableTablesSpec>
)

data class AvailableTablesSpec(
    val nrTables: Int,
    val day: LocalDate,
)