package com.graphite.competitionplanner.domain.mapping

import com.graphite.competitionplanner.club.domain.interfaces.ClubDTO
import com.graphite.competitionplanner.domain.entity.Club
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestMappingClubAndDTO {

    @Test
    fun testConvertDtoToEntity() {
        val dto = ClubDTO(1, "LULEÅ IK", "Sommarvägen 123")
        val club = Club(dto)

        Assertions.assertEquals(dto.id, club.id)
        Assertions.assertEquals(dto.name, club.name)
        Assertions.assertEquals(dto.address, club.address)
    }

    @Test
    fun testConvertEntityToDTO() {
        val club = Club(1, "LULEÅ IK", "Sommarvägen 123")
        val dto = ClubDTO(club)

        Assertions.assertEquals(club.id, dto.id)
        Assertions.assertEquals(club.name, dto.name)
        Assertions.assertEquals(club.address, dto.address)
    }
}