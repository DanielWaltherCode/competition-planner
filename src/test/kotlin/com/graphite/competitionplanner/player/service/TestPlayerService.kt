package com.graphite.competitionplanner.player.service

import com.graphite.competitionplanner.player.domain.*
import com.graphite.competitionplanner.player.repository.PlayerRepository
import com.graphite.competitionplanner.util.DataGenerator
import com.graphite.competitionplanner.util.TestHelper
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestPlayerService(
    @Autowired val playerRepository: PlayerRepository
) {

    private val dataGenerator = DataGenerator()

    private final val createPlayer = mock(CreatePlayer::class.java)
    private final val updatePlayer = mock(UpdatePlayer::class.java)
    private final val listAllPlayersInClub = mock(ListAllPlayersInClub::class.java)
    private final val deletePlayer = mock(DeletePlayer::class.java)
    private final val findPlayer = mock(FindPlayer::class.java)

    val playerService = PlayerService(
        playerRepository,
        createPlayer,
        updatePlayer,
        listAllPlayersInClub,
        deletePlayer,
        findPlayer
    )

    @Test
    fun shouldDelegateToUseCase() {
        // Act
        playerService.getPlayersByClubId(10)

        // Assert
        verify(listAllPlayersInClub, times(1)).execute(10)
        verify(listAllPlayersInClub, times(1)).execute(anyInt())
    }

    @Test
    fun shouldDelegateToCreatePlayerUseCase() {
        // Setup
        val player = dataGenerator.newNewPlayerDTO()

        // Act
        playerService.addPlayer(player)

        // Assert
        verify(createPlayer, times(1)).execute(player)
        verify(createPlayer, times(1)).execute(TestHelper.MockitoHelper.anyObject())
    }

    @Test
    fun shouldDelegateToUpdatePlayerUseCase() {
        // Setup
        val player = dataGenerator.newPlayerDTO()

        // ACt
        playerService.updatePlayer(player)

        // Assert
        verify(updatePlayer, times(1)).execute(player)
        verify(updatePlayer, times(1)).execute(TestHelper.MockitoHelper.anyObject())
    }

    @Test
    fun shouldDelegateToFindByIdUserCase() {
        // Act
        playerService.getPlayer(10)

        // Assert
        verify(findPlayer, times(1)).byId(10)
        verify(findPlayer, times(1)).byId(anyInt())
    }

    @Test
    fun shouldDelegateToFindByNameUseCase() {
        // Setup
        val searchString = "Niklas"

        // Act
        playerService.findByName(searchString)

        // Assert
        verify(findPlayer, times(1)).byPartName(searchString)
        verify(findPlayer, times(1)).byPartName(anyString())
    }

    @Test
    fun shouldDelegateToDeleteUseCase() {
        // Act
        playerService.deletePlayer(19)

        // Assert
        verify(deletePlayer, times(1)).execute(TestHelper.MockitoHelper.anyObject())
    }

}