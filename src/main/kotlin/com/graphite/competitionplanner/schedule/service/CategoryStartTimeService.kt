package com.graphite.competitionplanner.schedule.service

import com.fasterxml.jackson.annotation.JsonFormat
import com.graphite.competitionplanner.competition.domain.FindCompetitions
import com.graphite.competitionplanner.competition.domain.GetDaysOfCompetition
import com.graphite.competitionplanner.competitioncategory.domain.FindCompetitionCategory
import com.graphite.competitionplanner.competitioncategory.domain.GetCompetitionCategories
import com.graphite.competitionplanner.competitioncategory.interfaces.CompetitionCategoryDTO
import com.graphite.competitionplanner.schedule.api.CategoryStartTimeSpec
import com.graphite.competitionplanner.schedule.repository.ScheduleRepository
import com.graphite.competitionplanner.tables.records.ScheduleCategoryRecord
import org.jooq.exception.NoDataFoundException
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalTime

@Service
class CategoryStartTimeService(
    val scheduleRepository: ScheduleRepository,
    val findCompetitions: FindCompetitions,
    val getCompetitionCategories: GetCompetitionCategories,
    val getDaysOfCompetition: GetDaysOfCompetition,
    val findCompetitionCategory: FindCompetitionCategory
) {
    fun addCategoryStartTime(
        competitionCategoryId: Int,
        categoryStartTimeSpec: CategoryStartTimeSpec
    ): CategoryStartTimeDTO {
        val scheduleCategoryRecord =
            scheduleRepository.addCategoryStartTime(competitionCategoryId, categoryStartTimeSpec)
        return scheduleCategoryRecordToDTO(scheduleCategoryRecord)
    }

    private fun getStartTimeFormOptions(competitionId: Int): StartTimeFormOptions {
        val competition = findCompetitions.byId(competitionId)
        return StartTimeFormOptions(
            getDaysOfCompetition.execute(competition),
            StartInterval.values().asList()
        )
    }

    private fun scheduleCategoryRecordToDTO(scheduleCategoryRecord: ScheduleCategoryRecord): CategoryStartTimeDTO {
        val startInterval: StartInterval = if (scheduleCategoryRecord.startInterval == null) {
            StartInterval.NOT_SELECTED
        } else {
            StartInterval.valueOf(scheduleCategoryRecord.startInterval)
        }
        val competitionCategory =
            findCompetitionCategory.byId(scheduleCategoryRecord.competitonCategoryId)
        return CategoryStartTimeDTO(
            scheduleCategoryRecord.id,
            competitionCategory,
            scheduleCategoryRecord.playingDay,
            startInterval,
            scheduleCategoryRecord.exactStartTime
        )
    }

}

data class CategoryStartTimeDTO(
    val id: Int,
    val categoryDTO: CompetitionCategoryDTO,
    @JsonFormat(pattern = "yyyy-MM-dd")
    val playingDay: LocalDate?,
    val startInterval: StartInterval,
    @JsonFormat(pattern = "HH:mm")
    val exactStartTime: LocalTime?
)

data class CategoryStartTimesWithOptionsDTO(
    val categoryStartTimeList: List<CategoryStartTimeDTO>,
    val startTimeFormOptions: StartTimeFormOptions
)

data class StartTimeFormOptions(
    @JsonFormat(pattern = "yyyy-MM-dd")
    val availableDays: List<LocalDate>,
    val startIntervals: List<StartInterval>
)

enum class StartInterval {
    NOT_SELECTED, MORNING, AFTERNOON, EVENING
}