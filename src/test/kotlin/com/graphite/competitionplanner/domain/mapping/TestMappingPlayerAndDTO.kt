package com.graphite.competitionplanner.domain.mapping

import com.graphite.competitionplanner.domain.entity.Player
import com.graphite.competitionplanner.player.domain.interfaces.PlayerDTO
import com.graphite.competitionplanner.util.DataGenerator
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestMappingPlayerAndDTO {

    val dataGenerator = DataGenerator()

    @Test
    fun testConvertPlayerToDTO() {
        val player = dataGenerator.newPlayer()
        player.club = dataGenerator.newClub()
        val dto = PlayerDTO(player)

        Assertions.assertEquals(player.id, dto.id)
        Assertions.assertEquals(player.firstName, dto.firstName)
        Assertions.assertEquals(player.lastName, dto.lastName)
        Assertions.assertEquals(player.dateOfBirth, dto.dateOfBirth)
    }

    @Test
    fun testConvertPlayerDtoEntity() {
        val dto = dataGenerator.newPlayerDTO()
        val player = Player(dto)

        Assertions.assertEquals(dto.id, player.id)
        Assertions.assertEquals(dto.firstName, player.firstName)
        Assertions.assertEquals(dto.lastName, player.lastName)
        Assertions.assertEquals(dto.dateOfBirth, player.dateOfBirth)
    }
}