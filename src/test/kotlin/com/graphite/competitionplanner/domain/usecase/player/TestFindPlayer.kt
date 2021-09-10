package com.graphite.competitionplanner.domain.usecase.player

import com.graphite.competitionplanner.DataGenerator
import com.graphite.competitionplanner.domain.dto.ClubDTO
import com.graphite.competitionplanner.player.domain.interfaces.IPlayerRepository
import com.graphite.competitionplanner.club.domain.FindClub
import com.graphite.competitionplanner.player.domain.FindPlayer
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestFindPlayer {

    val dataGenerator = DataGenerator()

    private final val mockedPlayerRepository = mock(IPlayerRepository::class.java)
    private final val mockedFindClub = mock(FindClub::class.java)
    private val findPlayer = FindPlayer(mockedPlayerRepository, mockedFindClub)

    @Test
    fun shouldCallRepositoryWhenSearchingForId() {
        // Setup
        val playerDto = dataGenerator.newPlayerDTO()
        `when`(mockedFindClub.byId(playerDto.clubId)).thenReturn(ClubDTO(playerDto.clubId, "Ilö IK", "Smakgatan 1A"))
        `when`(mockedPlayerRepository.findById(playerDto.id)).thenReturn(playerDto)

        // Act
        findPlayer.byId(playerDto.id)

        // Verify
        verify(mockedPlayerRepository, times(1)).findById(playerDto.id)
        verify(mockedPlayerRepository, times(1)).findById(anyInt())
    }

    @Test
    fun shouldCallRepositoryWhenSearchingForName() {
        // Setup
        val searchString = "partName"

        // Act
        findPlayer.byPartName(searchString)

        // Verify
        verify(mockedPlayerRepository, times(1)).findByName(searchString)
        verify(mockedPlayerRepository, times(1)).findByName(anyString())
    }
}