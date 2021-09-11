package com.graphite.competitionplanner.competition.domain

import com.graphite.competitionplanner.competition.interfaces.ICompetitionRepository
import com.graphite.competitionplanner.util.DataGenerator
import com.graphite.competitionplanner.util.TestHelper
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.anyInt
import org.mockito.Mockito.times
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestUpdateCompetition {

    val dataGenerator = DataGenerator()
    private final val mockedRepository = Mockito.mock(ICompetitionRepository::class.java)
    val updateCompetition = UpdateCompetition(mockedRepository)

    @Test
    fun shouldDelegateToRepository() {
        // Setup
        val competitionId = 1334
        val spec = dataGenerator.newCompetitionUpdateSpec()

        // Act
        updateCompetition.execute(competitionId, spec)

        // Assert
        Mockito.verify(mockedRepository, times(1)).update(competitionId, spec)
        Mockito.verify(mockedRepository, times(1)).update(anyInt(), TestHelper.MockitoHelper.anyObject())
    }
}