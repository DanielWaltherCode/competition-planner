package com.graphite.competitionplanner.player.domain

import com.graphite.competitionplanner.club.domain.FindClub
import com.graphite.competitionplanner.club.interfaces.ClubDTO
import com.graphite.competitionplanner.common.exception.NotFoundException
import com.graphite.competitionplanner.player.interfaces.IPlayerRepository
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
    fun shouldNotCallStoreWhenIfClubDoesNotExist() {
        // Setup
        val spec = dataGenerator.newPlayerSpec()
        `when`(mockedFindClub.byId(spec.clubId)).thenThrow(NotFoundException(""))

        // Act
        Assertions.assertThrows(NotFoundException::class.java) { updatePlayer.execute(1, spec) }

        // Assert
        verify(mockedPlayerRepository, never()).store(TestHelper.MockitoHelper.anyObject())
    }

    @Test
    fun shouldAssertThatClubExist() {
        // Setup
        val spec = dataGenerator.newPlayerSpec()
        `when`(mockedFindClub.byId(spec.clubId)).thenReturn(ClubDTO(spec.clubId, "ClubName", "ClubAddress"))

        // Act
        updatePlayer.execute(1, spec)

        // Assert
        verify(mockedFindClub, atLeastOnce()).byId(spec.clubId)
    }
}