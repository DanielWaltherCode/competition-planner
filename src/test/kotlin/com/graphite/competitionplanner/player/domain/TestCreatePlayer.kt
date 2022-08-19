package com.graphite.competitionplanner.player.domain

import com.graphite.competitionplanner.club.domain.FindClub
import com.graphite.competitionplanner.club.interfaces.ClubDTO
import com.graphite.competitionplanner.common.exception.NotFoundException
import com.graphite.competitionplanner.player.interfaces.IPlayerRepository
import com.graphite.competitionplanner.player.interfaces.PlayerDTO
import com.graphite.competitionplanner.util.DataGenerator
import com.graphite.competitionplanner.util.TestHelper
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestCreatePlayer {

    val dataGenerator = DataGenerator()

    private final val mockedPlayerRepository = mock(IPlayerRepository::class.java)
    private final val mockedFindClub = mock(FindClub::class.java)
    private val createPlayer = CreatePlayer(mockedPlayerRepository, mockedFindClub)

    @Test
    fun shouldCallStoreWhenEntityIsOk() {
        // Setup
        val spec = dataGenerator.newPlayerSpec()
        `when`(mockedFindClub.byId(spec.clubId)).thenReturn(ClubDTO(spec.clubId, "Liljan IF", "Stovägen"))
        `when`(mockedPlayerRepository.store(TestHelper.MockitoHelper.anyObject(), TestHelper.MockitoHelper.anyObject())).thenReturn(
            PlayerDTO(
                1,
                spec.firstName,
                spec.lastName,
                spec.clubId,
                spec.dateOfBirth
            )
        )

        // Act
        createPlayer.execute(spec)

        // Assert
        verify(mockedPlayerRepository, times(1)).store(spec)
        verify(mockedPlayerRepository, times(1)).store(TestHelper.MockitoHelper.anyObject(), TestHelper.MockitoHelper.anyObject())
    }

    @Test
    fun shouldAssertThatClubExist() {
        // Setup
        val spec = dataGenerator.newPlayerSpec()
        `when`(mockedFindClub.byId(spec.clubId)).thenReturn(ClubDTO(spec.clubId, "Liljan IF", "Stovägen"))
        `when`(mockedPlayerRepository.store(TestHelper.MockitoHelper.anyObject(), TestHelper.MockitoHelper.anyObject())).thenReturn(
            PlayerDTO(
                1,
                spec.firstName,
                spec.lastName,
                spec.clubId,
                spec.dateOfBirth
            )
        )

        // Act
        createPlayer.execute(spec)

        // Assert
        verify(mockedFindClub, atLeastOnce()).byId(spec.clubId)
    }

    @Test
    fun shouldNotCallStoreUnlessClubHasBeenVerified() {
        // Setup
        val spec = dataGenerator.newPlayerSpec()
        `when`(mockedFindClub.byId(spec.clubId)).thenThrow(NotFoundException(""))

        // Act
        Assertions.assertThrows(NotFoundException::class.java) { createPlayer.execute(spec) }

        // Assert
        verify(mockedPlayerRepository, never()).store(TestHelper.MockitoHelper.anyObject(), TestHelper.MockitoHelper.anyObject())
    }
}