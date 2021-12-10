package com.graphite.competitionplanner.player.api

import com.graphite.competitionplanner.player.domain.*
import com.graphite.competitionplanner.player.service.PlayerService
import com.graphite.competitionplanner.registration.repository.RegistrationRepository
import com.graphite.competitionplanner.util.DataGenerator
import com.graphite.competitionplanner.util.TestHelper
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestPlayerApi {

    private val dataGenerator = DataGenerator()

    private val createPlayer = mock(CreatePlayer::class.java)
    private val updatePlayer = mock(UpdatePlayer::class.java)
    private val listAllPlayersInClub = mock(ListAllPlayersInClub::class.java)
    private val deletePlayer = mock(DeletePlayer::class.java)
    private val findPlayer = mock(FindPlayer::class.java)
    private val registrationRepository = mock(RegistrationRepository::class.java)

    val playerService = PlayerService(
        createPlayer,
        updatePlayer,
        listAllPlayersInClub,
        deletePlayer,
        findPlayer,
        registrationRepository
    )

    val api = PlayerApi(
        playerService,
        listAllPlayersInClub
    )

    @Test
    fun shouldDelegateToUseCase() {
        // Act
        api.getPlayersByClubId(10)

        // Assert
        verify(listAllPlayersInClub, times(1)).execute(10)
        verify(listAllPlayersInClub, times(1)).execute(anyInt())
    }

    @Test
    fun shouldDelegateToCreatePlayerUseCase() {
        // Setup
        val spec = dataGenerator.newPlayerSpec()

        // Act
        playerService.addPlayer(spec)

        // Assert
        verify(createPlayer, times(1)).execute(spec)
        verify(createPlayer, times(1)).execute(TestHelper.MockitoHelper.anyObject())
    }

    @Test
    fun shouldDelegateToUpdatePlayerUseCase() {
        // Setup
        val spec = dataGenerator.newPlayerSpec()

        // ACt
        playerService.updatePlayer(1, spec)

        // Assert
        verify(updatePlayer, times(1)).execute(1, spec)
        verify(updatePlayer, times(1)).execute(anyInt(), TestHelper.MockitoHelper.anyObject())
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
        verify(deletePlayer, times(1)).execute(anyInt())
    }

}