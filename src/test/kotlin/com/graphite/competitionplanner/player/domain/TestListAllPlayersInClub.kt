package com.graphite.competitionplanner.player.domain

import com.graphite.competitionplanner.player.domain.interfaces.IPlayerRepository
import com.graphite.competitionplanner.util.DataGenerator
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestListAllPlayersInClub {

    private final val mockedPlayerRepository = mock(IPlayerRepository::class.java)
    private val listAllPlayersInClub = ListAllPlayersInClub(mockedPlayerRepository)
    val dataGenerator = DataGenerator()

    @Test
    fun shouldDelegateToRepository() {
        // Setup
        val club = dataGenerator.newClubDTO()
        val players = listOf(
            dataGenerator.newPlayerWithClubDTO(firstName = "ola", clubDTO = club),
            dataGenerator.newPlayerWithClubDTO(firstName = "henrik", clubDTO = club),
            dataGenerator.newPlayerWithClubDTO(firstName = "matias", clubDTO = club)
        )
        `when`(mockedPlayerRepository.playersInClub(club.id)).thenReturn(players)

        // Act
        val foundPlayers = listAllPlayersInClub.execute(club.id)

        // Assert
        Assertions.assertEquals(players, foundPlayers)
        verify(mockedPlayerRepository, times(1)).playersInClub(club.id)
        verify(mockedPlayerRepository, times(1)).playersInClub(anyInt())
    }
}