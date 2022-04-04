package com.graphite.competitionplanner.schedule.domain

import com.fasterxml.jackson.annotation.JsonFormat
import com.graphite.competitionplanner.schedule.domain.interfaces.ScheduleSettingsDTO
import com.graphite.competitionplanner.schedule.interfaces.IScheduleRepository
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.LocalDateTime

@Component
class TrySchedule(
    val repository: IScheduleRepository
) {

    /**
     * Determines if we can successfully schedule all matches for the given competition categories
     * within the time interval
     *
     * @return True if all matches for all categories can be played within the time interval. Else false
     */
    fun execute(competitionId: Int, spec: PreScheduleSpec, settings: ScheduleSettingsDTO): PreScheduleDto {
        val matches = repository.getMatchesIn(competitionId, spec.playDate, spec.timeInterval)
        val competitionCategoryIds = matches.map { it.competitionCategoryId }.distinct()
//        val averageMatchTime = settings.averageMatchTime
//        val numberOfTables = settings.numberOfTables
        //TODO: Run algorithm for matches, averageMatchTime, and numberOfTables
        return PreScheduleDto(
            true,
            LocalDateTime.now(),
            spec.playDate,
            spec.timeInterval,
            competitionCategoryIds)
    }
}

/**
 * The match dto from a scheduling perspective. Contains necessary information used by the scheduling algorithm.
 */
data class ScheduleMatchDto(
    /**
     * ID of the match
     */
    val id: Int,

    /**
     * ID of the competition category that the match belongs to
     */
    val competitionCategoryId: Int,

    /**
     * IDs of players in first team
     */
    val firstTeamPlayerIds: List<Int>,

    /**
     * IDs of players in second team
     */
    val secondTeamPlayerIds: List<Int>
)

/**
 * Data class to pre-schedule a competition category for a given date and time interval
 */
data class PreScheduleSpec(
    /**
     * Date when the competition category should be played
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    val playDate: LocalDate,

    /**
     * Time interval for the categories to play
     */
    val timeInterval: TimeInterval,

    /**
     * ID of the competition category to add to the given time interval
     */
    val competitionCategoryId: Int
)

/**
 * The result of pre-scheduling a competition category to specific date and time interval.
 */
data class PreScheduleDto(
    /**
     * True if all matches for all pre-scheduled competition categories fit within the interval on the given date
     */
    val success: Boolean,

    /**
     * The estimated time when the last match is ending. This can be outside the time interval
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    val estimatedEndTime: LocalDateTime,

    /**
     * Date when the competition categories should be played
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    val playDate: LocalDate,

    /**
     * Time interval for when the competition categories should be played
     */
    val timeInterval: TimeInterval,

    /**
     * List of competition category ids pre-scheduled for the same date and time interval.
     */
    val competitionCategoryIds: List<Int>
)