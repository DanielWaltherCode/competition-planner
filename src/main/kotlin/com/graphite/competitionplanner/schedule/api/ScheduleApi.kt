package com.graphite.competitionplanner.schedule.api

import com.graphite.competitionplanner.category.interfaces.CategoryDTO
import com.graphite.competitionplanner.schedule.domain.interfaces.ExcelScheduleDTO
import com.graphite.competitionplanner.schedule.domain.interfaces.ExcelScheduleItemDTO
import com.graphite.competitionplanner.schedule.domain.interfaces.ExcelScheduleMatchDTO
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@RestController
@RequestMapping("/schedule/{competitionId}/")
class ScheduleApi {

    @GetMapping("excel-table")
    fun getScheduleOverview(): ExcelScheduleDTO {
        return generateDummyExcelSchedule()
    }


    private fun generateDummyExcelSchedule(): ExcelScheduleDTO {
        val scheduleItemList = mutableListOf<ExcelScheduleItemDTO>()
        scheduleItemList.add(generateDummyExcelScheduleItem(1, "Herrar 1", "morning", "Group"))
        scheduleItemList.add(generateDummyExcelScheduleItem(2, "Herrar 1", "morning", "Group"))
        scheduleItemList.add(generateDummyExcelScheduleItem(3, "Herrar 1", "morning", "Group"))
        scheduleItemList.add(generateDummyExcelScheduleItem(1, "Herrar 1", "afternoon", "Playoff"))
        scheduleItemList.add(generateDummyExcelScheduleItem(2, "Herrar 2", "afternoon", "Group"))
        scheduleItemList.add(generateDummyExcelScheduleItem(3, "Herrar 2", "afternoon", "Group"))
        scheduleItemList.add(generateDummyExcelScheduleItem(4, "Herrar 4", "morning", "Group"))
        scheduleItemList.add(generateDummyExcelScheduleItem(4, "Herrar 4", "afternoon", "Playoff"))
        scheduleItemList.add(generateDummyExcelScheduleItem(5, "Damer 1", "morning", "Group"))
        scheduleItemList.add(generateDummyExcelScheduleItem(5, "Flickor 17", "afternoon", "Group"))
        scheduleItemList.add(generateDummyExcelScheduleItem(6, "Damer 1", "morning", "Group"))
        scheduleItemList.add(generateDummyExcelScheduleItem(6, "Flickor 17", "afternoon", "Group"))
        scheduleItemList.add(generateDummyExcelScheduleItem(7, "Damer 1", "morning", "Group"))
        scheduleItemList.add(generateDummyExcelScheduleItem(7, "Damer 1", "afternoon", "Playoff"))

        scheduleItemList.add(generateDummyExcelScheduleItem(8, "Pojkar 15", "morning", "Group"))
        scheduleItemList.add(generateDummyExcelScheduleItem(9, "Pojkar 15", "morning", "Group"))
        scheduleItemList.add(generateDummyExcelScheduleItem(10, "Pojkar 15", "morning", "Group"))
        scheduleItemList.add(generateDummyExcelScheduleItem(11, "Pojkar 15", "morning", "Group"))
        scheduleItemList.add(generateDummyExcelScheduleItem(12, "Pojkar 15", "morning", "Group"))
        scheduleItemList.add(generateDummyExcelScheduleItem(13, "Flickor 13", "morning", "Group"))
        scheduleItemList.add(generateDummyExcelScheduleItem(14, "Pojkar 11", "morning", "Group"))
        scheduleItemList.add(generateDummyExcelScheduleItem(15, "Pojkar 11", "morning", "Group"))

        scheduleItemList.add(generateDummyExcelScheduleItem(8,  "Pojkar 15", "afternoon", "Playoff"))
        scheduleItemList.add(generateDummyExcelScheduleItem(9, "Pojkar 15", "afternoon", "Playoff"))
        scheduleItemList.add(generateDummyExcelScheduleItem(10, "Pojkar 15", "afternoon", "Playoff"))
        scheduleItemList.add(generateDummyExcelScheduleItem(11, "Pojkar 11", "afternoon", "Playoff"))
        scheduleItemList.add(generateDummyExcelScheduleItem(12, "Pojkar 11", "afternoon", "Playoff"))
        scheduleItemList.add(generateDummyExcelScheduleItem(13, "Flickor 13", "afternoon", "Playoff"))
        scheduleItemList.add(generateDummyExcelScheduleItem(14, "Flickor 11", "afternoon", "Group"))
        scheduleItemList.add(generateDummyExcelScheduleItem(15, "Pojkar 12", "afternoon", "Playoff"))

        val tables = scheduleItemList.map { it.tableNumber }.distinct()
        val sortedScheduleItemList = mutableListOf<ExcelScheduleItemDTO>()

        for (table in tables) {
            val matchesAtTable = mutableListOf<ExcelScheduleMatchDTO>()
            val matchingItems = scheduleItemList.filter { it.tableNumber == table }
            for (item in matchingItems) {
                matchesAtTable.addAll(item.matchesAtTable)
            }
            val sortedRows = matchesAtTable.sortedBy { it.startTime }
            sortedScheduleItemList.add(ExcelScheduleItemDTO(table, sortedRows))
        }
        val validStartTimes = sortedScheduleItemList[0].matchesAtTable.map { it.startTime }.toSet()
        val distinctCategories = mutableSetOf<CategoryDTO>()
        for (item in sortedScheduleItemList) {
            for (match in item.matchesAtTable) {
                distinctCategories.add(match.category)
            }
        }
        return ExcelScheduleDTO(sortedScheduleItemList, validStartTimes, distinctCategories)
    }

    private fun generateDummyExcelScheduleItem(
        tableNumber: Number,
        categoryName: String,
        timeOfDay: String,
        groupOrRound: String
    ): ExcelScheduleItemDTO {
        val times = getTimeSlots()

        val scheduleRowList: MutableList<ExcelScheduleMatchDTO> = mutableListOf()
        for (i in 0..10) {
            var index = i
            if (timeOfDay == "afternoon") {
                index = i + 9
            }
            // Skip some for testing purposes
            if(tableNumber == 11 || tableNumber == 7) {
                if (index == 3 || index == 9) {
                    continue
                }
            }
            scheduleRowList.add(
                ExcelScheduleMatchDTO(
                    times[index],
                    CategoryDTO(-1, categoryName, "Singles"),
                    groupOrRound
                )
            )
        }
        return ExcelScheduleItemDTO(tableNumber, scheduleRowList)
    }


    private fun getTimeSlots(): List<LocalDateTime> {
        return listOf(
            LocalDateTime.of(LocalDate.now(), LocalTime.of(9, 0)),
            LocalDateTime.of(LocalDate.now(), LocalTime.of(9, 25)),
            LocalDateTime.of(LocalDate.now(), LocalTime.of(9, 50)),
            LocalDateTime.of(LocalDate.now(), LocalTime.of(10, 15)),
            LocalDateTime.of(LocalDate.now(), LocalTime.of(10, 40)),
            LocalDateTime.of(LocalDate.now(), LocalTime.of(11, 5)),
            LocalDateTime.of(LocalDate.now(), LocalTime.of(11, 30)),
            LocalDateTime.of(LocalDate.now(), LocalTime.of(11, 55)),
            LocalDateTime.of(LocalDate.now(), LocalTime.of(12, 20)),
            LocalDateTime.of(LocalDate.now(), LocalTime.of(12, 45)),
            LocalDateTime.of(LocalDate.now(), LocalTime.of(13, 10)),
            LocalDateTime.of(LocalDate.now(), LocalTime.of(13, 35)),
            LocalDateTime.of(LocalDate.now(), LocalTime.of(14, 0)),
            LocalDateTime.of(LocalDate.now(), LocalTime.of(14, 25)),
            LocalDateTime.of(LocalDate.now(), LocalTime.of(14, 50)),
            LocalDateTime.of(LocalDate.now(), LocalTime.of(15, 15)),
            LocalDateTime.of(LocalDate.now(), LocalTime.of(15, 40)),
            LocalDateTime.of(LocalDate.now(), LocalTime.of(16, 5)),
            LocalDateTime.of(LocalDate.now(), LocalTime.of(16, 30)),
            LocalDateTime.of(LocalDate.now(), LocalTime.of(16, 55)),
        )
    }
}