package com.graphite.competitionplanner.domain.mapping

import com.graphite.competitionplanner.domain.dto.CategoryDTO
import com.graphite.competitionplanner.domain.entity.Category
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestMappingCategoryAndDTO {

    @Test
    fun testConvertDtoToEntity() {
        val dto = CategoryDTO(1, "HERRAR-1")
        val category = Category(dto)

        Assertions.assertEquals(dto.id, category.id)
        Assertions.assertEquals(dto.name, category.name)
    }

    @Test
    fun testConvertEntityToDto() {
        val entity = Category(333, "DAMER-1")
        val dto = CategoryDTO(entity)

        Assertions.assertEquals(entity.id, dto.id)
        Assertions.assertEquals(entity.name, dto.name)
    }
}