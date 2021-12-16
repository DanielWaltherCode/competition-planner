package com.graphite.competitionplanner.competition.domain

import com.graphite.competitionplanner.competition.interfaces.ICompetitionRepository
import com.graphite.competitionplanner.util.DataGenerator
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestListAllCompetitions {

    private val mockedCompetitionRepository = mock(ICompetitionRepository::class.java)
    private val findCompetitions = FindCompetitions(mockedCompetitionRepository)
    val dataGenerator = DataGenerator()

    @Test
    fun shouldCallRepositoryWithTheGivenClubId() {
        // Setup
        val club = dataGenerator.newClubDTO(id = 100)

        // Act
        findCompetitions.thatBelongTo(club.id)

        // Assert
        verify(mockedCompetitionRepository, times(1)).findCompetitionsThatBelongsTo(club.id)
        verify(mockedCompetitionRepository, times(1)).findCompetitionsThatBelongsTo(anyInt())
    }
}