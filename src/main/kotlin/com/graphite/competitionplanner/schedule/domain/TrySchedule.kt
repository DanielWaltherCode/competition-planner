package com.graphite.competitionplanner.schedule.domain

import com.fasterxml.jackson.annotation.JsonFormat
import com.graphite.competitionplanner.schedule.interfaces.ScheduleSettingsDTO
import com.graphite.competitionplanner.schedule.interfaces.IScheduleRepository
import com.graphite.competitionplanner.schedule.service.StartInterval
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.concurrent.TimeUnit
import kotlin.time.Duration
import kotlin.time.toDuration

@Component
class TrySchedule(
    val repository: IScheduleRepository,
    val createSchedule: CreateSchedule
) {

    /**
     * Determines if we can successfully schedule all matches for the given competition categories
     * within the time interval
     *
     * @return True if all matches for all categories can be played within the time interval. Else false
     */
    fun execute(competitionId: Int, competitionCategoryId: Int, spec: PreScheduleSpec, settings: ScheduleSettingsDTO): PreScheduleDto {
        // TODO: We might want to guard against the spec having a play date that is not consistent with when the competition is taking place
        // TODO: We now have to handle the NOT_SELECTED time interval case
        // TODO: When we change from e.g. MORNING -> EVENING we also need to update status of the categories in MORNING as they may fit now.

        repository.storePreSchedule(competitionId, competitionCategoryId, spec)
        val matches = repository.getPreScheduledMatches(competitionId, spec.playDate, spec.timeInterval)
        val competitionCategoryIds = matches.map { it.competitionCategoryId }.distinct()
        val schedule = createSchedule.execute(matches, settings)

        val numberOfTimeslots = schedule.timeslots.count()
        val durationOfInterval = spec.timeInterval.toDuration()
        val durationRequiredToFitAllTimeslots = settings.averageMatchTime.times(numberOfTimeslots)

        val enoughTime = durationOfInterval > durationRequiredToFitAllTimeslots
        val estimatedEndTime = LocalDateTime.of(spec.playDate, spec.timeInterval.startTime())
            .plusMinutes(durationRequiredToFitAllTimeslots.inWholeMinutes)

        repository.update(competitionCategoryIds, estimatedEndTime, enoughTime)

        return PreScheduleDto(
            enoughTime,
            estimatedEndTime, // TODO: Consider if we should use UTC?
            spec.playDate,
            spec.timeInterval,
            competitionCategoryIds
        )
    }

    fun StartInterval.toDuration(): Duration {
        return when (this) {
            StartInterval.MORNING -> (13 - 9).toDuration(TimeUnit.HOURS)
            StartInterval.AFTERNOON -> (17 - 13).toDuration(TimeUnit.HOURS)
            StartInterval.EVENING -> (21 - 17).toDuration(TimeUnit.HOURS)
            else -> 0.toDuration(TimeUnit.HOURS)
        }
    }

    fun StartInterval.startTime(): LocalTime {
        return when (this) {
            StartInterval.MORNING -> LocalTime.of(9, 0, 0)
            StartInterval.AFTERNOON -> LocalTime.of(13, 0, 0)
            StartInterval.EVENING -> LocalTime.of(17, 0, 0)
            else -> LocalTime.of(0, 0, 0)
        }
    }
}

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
    val timeInterval: StartInterval,
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
    val timeInterval: StartInterval,

    /**
     * List of competition category ids pre-scheduled for the same date and time interval.
     */
    val competitionCategoryIds: List<Int>
)