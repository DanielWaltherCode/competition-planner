package com.graphite.competitionplanner.schedule.domain

import com.graphite.competitionplanner.common.plusDuration
import com.graphite.competitionplanner.competition.domain.FindCompetitions
import com.graphite.competitionplanner.competitioncategory.interfaces.ICompetitionCategoryRepository
import com.graphite.competitionplanner.match.repository.MatchRepository
import com.graphite.competitionplanner.schedule.interfaces.IScheduleRepository
import com.graphite.competitionplanner.schedule.interfaces.TimeTableSlotSpec
import com.graphite.competitionplanner.schedule.service.AvailableTablesService
import com.graphite.competitionplanner.schedule.service.DailyStartEndService
import com.graphite.competitionplanner.schedule.service.ScheduleMetadataService
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import kotlin.time.Duration


/**
 * This is the class that handles all the modifications to the TimeTableSlots. Operations that alter these slots can be
 * - Changing play date
 * - Changing average time per game
 * - Adding lunch breaks
 * - Changing number of tables available
 *
 * Any operations to the TimeTableSlots will override any unpublished schema.
 */
@Component
class TimeTableSlotHandler(
        val scheduleRepository: IScheduleRepository,
        val scheduleAvailableTablesService: AvailableTablesService,
        val scheduleMetadataService: ScheduleMetadataService,
        val dailyStartEndService: DailyStartEndService,
        val findCompetitions: FindCompetitions,
        val competitionCategoryRepository: ICompetitionCategoryRepository,
        val matchRepository: MatchRepository,
) {

    fun execute(competitionId: Int) {
        val nrTables = scheduleAvailableTablesService.getTablesAvailable(competitionId)
        val matchDuration = scheduleMetadataService.getScheduleMetadata(competitionId).minutesPerMatch
        val dailyStartAndEndList = dailyStartEndService.getDailyStartAndEndForWholeCompetition(competitionId).dailyStartEndList
        val competitionInfo = findCompetitions.byId(competitionId)
        val timeSlots = mutableListOf<TimeTableSlotSpec>()

        for (dailyStartEndDTO in dailyStartAndEndList) {
            val validStartTimes = generateStartTimes(
                LocalDateTime.of(dailyStartEndDTO.day, dailyStartEndDTO.startTime),
                LocalDateTime.of(dailyStartEndDTO.day, dailyStartEndDTO.endTime),
                Duration.minutes(matchDuration)
            )
            val dailyNrTables = nrTables.first { it.day == dailyStartEndDTO.day }

            timeSlots.addAll(validStartTimes.flatMap {startTime ->
                (1..dailyNrTables.nrTables).map { tableNumber ->
                    TimeTableSlotSpec(
                            startTime,
                            tableNumber,
                            competitionInfo.location.name
                    )
                }

            })
        }

        with(scheduleRepository) {
            asTransaction {
                clearSchedule(competitionId)
                deleteTimeTable(competitionId)
                storeTimeTable(competitionId, timeSlots)
            }
        }
    }

    private fun generateStartTimes(
        startTime: LocalDateTime,
        endTime: LocalDateTime,
        estimatedMatchTime: Duration
    ): List<LocalDateTime> {
        val startTimes = mutableListOf<LocalDateTime>()
        var currentTime = startTime
        while (currentTime < endTime) {
            startTimes.add(currentTime)
            currentTime = currentTime.plusDuration(estimatedMatchTime)
        }
        return startTimes
    }
}

