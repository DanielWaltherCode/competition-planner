package com.graphite.competitionplanner.domain.mapping

import com.graphite.competitionplanner.domain.dto.CategoryDTO
import com.graphite.competitionplanner.domain.entity.Category
import com.graphite.competitionplanner.domain.entity.CategoryType
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestMappingCategoryAndDTO {

    @Test
    fun testConvertDtoToEntity() {
        val dto = CategoryDTO(1, "HERRAR-1", "SINGLES")
        val category = Category(dto)

        Assertions.assertEquals(dto.id, category.id)
        Assertions.assertEquals(dto.name, category.name)
        Assertions.assertEquals(dto.type, category.type.name)
    }

    @Test
    fun testConvertEntityToDto() {
        val entity = Category(333, "DAMER-1", CategoryType.SINGLES)
        val dto = CategoryDTO(entity)

        Assertions.assertEquals(entity.id, dto.id)
        Assertions.assertEquals(entity.name, dto.name)
        Assertions.assertEquals(entity.type.name, dto.type)
    }
}