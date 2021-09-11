package com.graphite.competitionplanner.player.domain

import com.graphite.competitionplanner.player.interfaces.IPlayerRepository
import com.graphite.competitionplanner.util.DataGenerator
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestDeletePlayer {

    private final val mockedPlayerRepository = mock(IPlayerRepository::class.java)
    val deletePlayer = DeletePlayer(mockedPlayerRepository)

    @Test
    fun shouldCallRepository() {
        // Setup
        val playerId = 10

        // Act
        deletePlayer.execute(playerId)

        // Verify
        verify(mockedPlayerRepository, times(1)).delete(playerId)
        verify(mockedPlayerRepository, times(1)).delete(anyInt())
    }

    @Test
    fun shouldReturnTheDeletedPlayer() {
        // Setup
        val dto = DataGenerator().newPlayerDTO()
        `when`(mockedPlayerRepository.delete(dto.id)).thenReturn(dto)

        // Act
        val deletedPlayer = deletePlayer.execute(dto.id)

        // Assert
        Assertions.assertEquals(dto, deletedPlayer)
    }
}