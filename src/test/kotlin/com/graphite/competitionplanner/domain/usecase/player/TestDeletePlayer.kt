package com.graphite.competitionplanner.domain.usecase.player

import com.graphite.competitionplanner.DataGenerator
import com.graphite.competitionplanner.TestHelper
import com.graphite.competitionplanner.player.domain.interfaces.IPlayerRepository
import com.graphite.competitionplanner.player.domain.DeletePlayer
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
        val dto = DataGenerator().newPlayerDTO()

        // Act
        deletePlayer.execute(dto)

        // Verify
        verify(mockedPlayerRepository, times(1)).delete(dto)
        verify(mockedPlayerRepository, times(1)).delete(TestHelper.MockitoHelper.anyObject())
    }

    @Test
    fun shouldReturnTheDeletedPlayer() {
        // Setup
        val dto = DataGenerator().newPlayerDTO()
        `when`(mockedPlayerRepository.delete(dto)).thenReturn(dto)

        // Act
        val deletedPlayer = deletePlayer.execute(dto)

        // Assert
        Assertions.assertEquals(dto, deletedPlayer)
    }
}