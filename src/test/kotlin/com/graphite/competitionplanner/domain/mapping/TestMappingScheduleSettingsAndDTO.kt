package com.graphite.competitionplanner.domain.mapping

import com.graphite.competitionplanner.domain.dto.ScheduleSettingsDTO
import com.graphite.competitionplanner.domain.entity.ScheduleSettings
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDateTime

@SpringBootTest
class TestMappingScheduleSettingsAndDTO {

    @Test
    fun testConvertDtoToEntity() {
        val dto = ScheduleSettingsDTO(10, 5, LocalDateTime.now())
        val entity = ScheduleSettings(dto)

        Assertions.assertEquals(dto.averageMatchTime, entity.averageMatchTime)
        Assertions.assertEquals(dto.numberOfTables, entity.numberOfTables)
    }

    @Test
    fun testConvertEntityDToDto() {
        val entity = ScheduleSettings(10, 10, LocalDateTime.now())
        val dto = ScheduleSettingsDTO(entity)

        Assertions.assertEquals(entity.averageMatchTime, dto.averageMatchTime)
        Assertions.assertEquals(entity.numberOfTables, dto.numberOfTables)
    }
}