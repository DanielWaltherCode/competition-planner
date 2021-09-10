package com.graphite.competitionplanner.domain.usecase.competition

import com.graphite.competitionplanner.DataGenerator
import com.graphite.competitionplanner.competition.domain.FindCompetitions
import com.graphite.competitionplanner.competition.domain.interfaces.ICompetitionRepository
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestListAllCompetitions {

    private final val mockedCompetitionRepository = mock(ICompetitionRepository::class.java)
    private val findCompetitions = FindCompetitions(mockedCompetitionRepository)
    val dataGenerator = DataGenerator()

    @Test
    fun shouldCallRepositoryWithTheGivenClubId() {
        // Setup
        val club = dataGenerator.newClubDTO(id = 100)

        // Act
        findCompetitions.thatBelongsTo(club.id)

        // Assert
        verify(mockedCompetitionRepository, times(1)).findCompetitionsFor(club.id)
        verify(mockedCompetitionRepository, times(1)).findCompetitionsFor(anyInt())
    }
}