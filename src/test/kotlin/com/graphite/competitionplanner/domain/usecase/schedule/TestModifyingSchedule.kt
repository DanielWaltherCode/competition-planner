package com.graphite.competitionplanner.domain.usecase.schedule

import com.graphite.competitionplanner.DataGenerator
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
        val original = createSchedule.execute(pool2, dataGenerator.newScheduleSettings(4))

        val newSettings = dataGenerator.newScheduleSettings(1)
        val modified = modifySchedule.execute(original, newSettings)

        val originalMatchIds = original.timeslots.flatMap { it.matches.map { matchDTO -> matchDTO.id } }
        val matchIdsInModifiedSchedule = modified.timeslots.flatMap { it.matches.map { matchDTO -> matchDTO.id } }

        Assertions.assertEquals(originalMatchIds, matchIdsInModifiedSchedule)
    }

    @Test
    fun modifyingAndCreatingAreEqual() {
        val matches = pool1 + pool2
        val settings = dataGenerator.newScheduleSettings(3)
        val schedule = createSchedule.execute(matches, settings)

        val scheduleToBeModified = createSchedule.execute(matches, dataGenerator.newScheduleSettings(4))

        val modified = modifySchedule.execute(scheduleToBeModified, settings)

        Assertions.assertEquals(schedule, modified)
    }

    @Test
    fun whenModifyingScheduleToZeroTablesItShouldThrowIllegalArgumentException() {
        val matches = pool1 + pool3
        val schedule = createSchedule.execute(matches, dataGenerator.newScheduleSettings(3))
        val newSettings = dataGenerator.newScheduleSettings(0)

        Assertions.assertThrows(IllegalArgumentException::class.java) { modifySchedule.execute(schedule, newSettings) }
    }

}