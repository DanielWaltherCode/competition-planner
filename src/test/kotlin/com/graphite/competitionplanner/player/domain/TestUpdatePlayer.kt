package com.graphite.competitionplanner.player.domain

import com.graphite.competitionplanner.club.domain.FindClub
import com.graphite.competitionplanner.club.domain.interfaces.ClubDTO
import com.graphite.competitionplanner.common.exception.NotFoundException
import com.graphite.competitionplanner.player.domain.interfaces.IPlayerRepository
import com.graphite.competitionplanner.util.DataGenerator
import com.graphite.competitionplanner.util.TestHelper
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestUpdatePlayer {

    val dataGenerator = DataGenerator()
    private final val mockedPlayerRepository = mock(IPlayerRepository::class.java)
    private final val mockedFindClub = mock(FindClub::class.java)
    val updatePlayer = UpdatePlayer(mockedPlayerRepository, mockedFindClub)

    @Test
    fun shouldCallStoreWhenEntityIsOk() {
        // Setup
        val dto = dataGenerator.newPlayerDTO()
        `when`(mockedFindClub.byId(dto.clubId)).thenReturn(ClubDTO(dto.clubId, "ClubName", "ClubAddress"))

        // Act
        updatePlayer.execute(dto)

        // Assert
        verify(mockedPlayerRepository, times(1)).update(TestHelper.MockitoHelper.anyObject())
    }

    @Test
    fun shouldNotCallStoreWhenEntityIsInvalid() {
        // Setup
        val dto = dataGenerator.newPlayerDTO(firstName = "Per123")

        // Act
        Assertions.assertThrows(IllegalArgumentException::class.java) { updatePlayer.execute(dto) }

        // Assert
        verify(mockedPlayerRepository, never()).store(TestHelper.MockitoHelper.anyObject())
    }

    @Test
    fun shouldNotCallStoreWhenIfClubDoesNotExist() {
        // Setup
        val dto = dataGenerator.newPlayerDTO()
        `when`(mockedFindClub.byId(dto.clubId)).thenThrow(NotFoundException(""))

        // Act
        Assertions.assertThrows(NotFoundException::class.java) { updatePlayer.execute(dto) }

        // Assert
        verify(mockedPlayerRepository, never()).store(TestHelper.MockitoHelper.anyObject())
    }

    @Test
    fun shouldAssertThatClubExist() {
        // Setup
        val dto = dataGenerator.newPlayerDTO()
        `when`(mockedFindClub.byId(dto.clubId)).thenReturn(ClubDTO(dto.clubId, "ClubName", "ClubAddress"))

        // Act
        updatePlayer.execute(dto)

        // Assert
        verify(mockedFindClub, atLeastOnce()).byId(dto.clubId)
    }
}