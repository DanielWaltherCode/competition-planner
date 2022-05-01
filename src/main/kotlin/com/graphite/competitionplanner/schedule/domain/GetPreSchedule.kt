package com.graphite.competitionplanner.schedule.domain

import com.fasterxml.jackson.annotation.JsonFormat
import com.graphite.competitionplanner.competitioncategory.repository.CompetitionCategory
import com.graphite.competitionplanner.schedule.interfaces.IScheduleRepository
import com.graphite.competitionplanner.schedule.service.StartInterval
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.LocalDateTime

@Component
class GetPreSchedule(
    val repository: IScheduleRepository
) {

    /**
     * Return the current pre-schedule for the competition
     */
    fun execute(competitionId: Int): List<CompetitionCategoryPreSchedule> {
        return repository.getPreSchedule(competitionId)
    }
}

data class CompetitionCategoryPreSchedule(
    /**
     * True if all matches for all pre-scheduled competition categories fit within the interval on the given date
     */
    val success: Boolean,

    /**
     * The estimated time when the last match is ending. This can be outside the time interval. If this value is null
     * it means that no estimated time have been calculated yet.
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    val estimatedEndTime: LocalDateTime?,

    /**
     * Date when the competition categories should be played
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    val playDate: LocalDate,

    /**
     * Time interval for when the competition categories should be played
     */
    val timeInterval: StartInterval,

    /**
     * The competition category
     */
    val competitionCategory: CompetitionCategory
)