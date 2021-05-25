package com.graphite.competitionplanner.domain.mapping

import com.graphite.competitionplanner.domain.dto.ClubDTO
import com.graphite.competitionplanner.domain.dto.PlayerEntityDTO
import com.graphite.competitionplanner.domain.entity.Club
import com.graphite.competitionplanner.domain.entity.Player
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDate

@SpringBootTest
class TestMappingPlayerAndDTO {

    @Test
    fun testConvertPlayerEntityToDTO() {
        val player = Player(
            33, "Test", "Testsson",
            Club(3, "ClubA", "Address 1"),
            LocalDate.of(1992, 10, 18)
        )
        val dto = PlayerEntityDTO(player)

        Assertions.assertEquals(player.id, dto.id)
        Assertions.assertEquals(player.firstName, dto.firstName)
        Assertions.assertEquals(player.lastName, dto.lastName)
        Assertions.assertEquals(player.club.id, dto.club.id)
        Assertions.assertEquals(player.club.name, dto.club.name)
        Assertions.assertEquals(player.club.address, dto.club.address)
        Assertions.assertEquals(player.dateOfBirth, dto.dateOfBirth)
    }

    @Test
    fun testConvertDtoToPlayerEntity() {
        val dto = PlayerEntityDTO(
            111, "Test", "Isaksson",
            ClubDTO(6, "Lyseskil", "Bakgården 1"),
            LocalDate.of(2001, 3, 12)
        )
        val player = Player(dto)

        Assertions.assertEquals(dto.id, player.id)
        Assertions.assertEquals(dto.firstName, player.firstName)
        Assertions.assertEquals(dto.lastName, player.lastName)
        Assertions.assertEquals(dto.club.id, player.club.id)
        Assertions.assertEquals(dto.club.name, player.club.name)
        Assertions.assertEquals(dto.club.address, player.club.address)
        Assertions.assertEquals(dto.dateOfBirth, player.dateOfBirth)
    }
}