package com.graphite.competitionplanner.schedule.domain

import com.graphite.competitionplanner.schedule.interfaces.IScheduleRepository
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class ModifyTimeTable(
    val repository: IScheduleRepository
) {

    fun addMatchToTimeTableSlot(tableSlotId: Int, matchId: Int): TimeTableSlotDto {
        val matchesInSameSlot = repository.addMatchToTimeTableSlot(tableSlotId, matchId)

        return TimeTableSlotDto(
            matchesInSameSlot.first().timeTableSlotId,
            matchesInSameSlot.first().startTime,
            matchesInSameSlot.first().tableNumber,
            matchesInSameSlot.first().location,
            matchesInSameSlot.size > 1,
            matchesInSameSlot.map {
                MatchInfo(
                    it.matchId,
                    it.competitionCategoryId
                )
            }
        )
    }
}

data class MatchToTimeTableSlot(
    val matchId: Int,
    val competitionCategoryId: Int,
    val timeTableSlotId: Int,
    val startTime: LocalDateTime,
    val tableNumber: Int,
    val location: String,
)