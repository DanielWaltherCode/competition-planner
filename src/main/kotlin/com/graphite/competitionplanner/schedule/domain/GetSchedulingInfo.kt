package com.graphite.competitionplanner.schedule.domain

import com.graphite.competitionplanner.competitioncategory.interfaces.CompetitionCategoryDTO
import com.graphite.competitionplanner.schedule.domain.interfaces.ScheduleSettingsDTO
import org.springframework.stereotype.Component

@Component
class GetSchedulingInfo {

    /**
     *
     */
    fun execute(competitionCategoryDTO: CompetitionCategoryDTO, settingsDTO: ScheduleSettingsDTO): SchedulingInfoDto {
        return SchedulingInfoDto(1,1,1)
    }

}

data class SchedulingInfoDto(
    val competitionCategoryId: Int,
    val tablesUsedInPool: Int,
    val timeslotsUsedInPool: Int
)