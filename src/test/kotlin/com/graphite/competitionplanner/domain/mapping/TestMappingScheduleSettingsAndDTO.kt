package com.graphite.competitionplanner.domain.mapping

import com.graphite.competitionplanner.DataGenerator
import com.graphite.competitionplanner.schedule.domain.interfaces.ScheduleSettingsDTO
import com.graphite.competitionplanner.domain.entity.ScheduleSettings
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestMappingScheduleSettingsAndDTO {

    val dataGenerator = DataGenerator()

    @Test
    fun testConvertDtoToEntity() {
        val dto = dataGenerator.newScheduleSettingsDTO()
        val entity = ScheduleSettings(dto)

        Assertions.assertEquals(dto.averageMatchTime, entity.averageMatchTime)
        Assertions.assertEquals(dto.numberOfTables, entity.numberOfTables)
        Assertions.assertEquals(dto.startTime, entity.startTime)
        Assertions.assertEquals(dto.endTime, entity.endTime)
    }

    @Test
    fun testConvertEntityDToDto() {
        val entity = dataGenerator.newScheduleSettings()
        val dto = ScheduleSettingsDTO(entity)

        Assertions.assertEquals(entity.averageMatchTime, dto.averageMatchTime)
        Assertions.assertEquals(entity.numberOfTables, dto.numberOfTables)
        Assertions.assertEquals(entity.startTime, dto.startTime)
        Assertions.assertEquals(entity.endTime, dto.endTime)
    }
}