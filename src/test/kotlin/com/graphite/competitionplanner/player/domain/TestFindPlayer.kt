package com.graphite.competitionplanner.player.domain

import com.graphite.competitionplanner.club.domain.FindClub
import com.graphite.competitionplanner.club.interfaces.ClubDTO
import com.graphite.competitionplanner.player.interfaces.IPlayerRepository
import com.graphite.competitionplanner.util.DataGenerator
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestFindPlayer {

    val dataGenerator = DataGenerator()

    private val mockedPlayerRepository = mock(IPlayerRepository::class.java)
    private val mockedFindClub = mock(FindClub::class.java)
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

    @Test
    fun shouldCallRepositoryWhenSearchingForNameInCompetition() {
        // Setup
        val searchString = "partName"
        val competition = dataGenerator.newCompetitionDTO()

        // Act
        findPlayer.byPartNameInCompetition(searchString, competition)

        // Verify
        verify(mockedPlayerRepository, times(1)).findByNameInCompetition(searchString, competition.id)
        verify(mockedPlayerRepository, times(1)).findByNameInCompetition(anyString(), anyInt())
    }
}