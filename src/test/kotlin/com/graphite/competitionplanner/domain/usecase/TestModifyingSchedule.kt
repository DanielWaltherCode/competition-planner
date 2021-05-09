package com.graphite.competitionplanner.domain.usecase

import com.graphite.competitionplanner.domain.DataGenerator
import com.graphite.competitionplanner.domain.dto.ScheduleSettingsDTO
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.lang.IllegalArgumentException

@SpringBootTest
class TestModifyingSchedule(
    @Autowired val modifySchedule: ModifySchedule,
    @Autowired val createSchedule: CreateSchedule
) {

    private final val dataGenerator = DataGenerator()
    val pool1 = dataGenerator.pool1()
    val pool2 = dataGenerator.pool2()
    val pool3 = dataGenerator.pool3()

    @Test
    fun whenModifyingNumberOfTablesThenOriginalMatchesShouldRemain() {
        val original = createSchedule.execute(pool2, ScheduleSettingsDTO(15, 4))

        val newSettings = ScheduleSettingsDTO(10, 1)
        val modified = modifySchedule.execute(original, newSettings)

        val matchesInOriginalSchedule = original.timeslots.flatMap { it.matches }
        val matchesInModifiedSchedule = modified.timeslots.flatMap { it.matches }

        Assertions.assertEquals(matchesInOriginalSchedule, matchesInModifiedSchedule)
    }

    @Test
    fun modifyingAndCreatingAreEqual() {
        val matches = pool1 + pool2
        val schedule = createSchedule.execute(matches, ScheduleSettingsDTO(15, 3))

        val scheduleToBeModified = createSchedule.execute(matches, ScheduleSettingsDTO(15, 4))

        val newSettings = ScheduleSettingsDTO(15, 3)
        val modified = modifySchedule.execute(scheduleToBeModified, newSettings)

        Assertions.assertEquals(schedule, modified)
    }

    @Test
    fun whenModifyingScheduleToZeroTablesItShouldThrowIllegalArgumentException() {
        val matches = pool1 + pool3
        val schedule = createSchedule.execute(matches, ScheduleSettingsDTO(15, 3))
        val newSettings = ScheduleSettingsDTO(10, 0)

        Assertions.assertThrows(IllegalArgumentException::class.java) { modifySchedule.execute(schedule, newSettings) }
    }

}