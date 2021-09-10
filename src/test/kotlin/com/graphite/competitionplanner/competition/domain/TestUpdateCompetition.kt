package com.graphite.competitionplanner.competition.domain

import com.graphite.competitionplanner.competition.domain.interfaces.ICompetitionRepository
import com.graphite.competitionplanner.util.DataGenerator
import com.graphite.competitionplanner.util.TestHelper
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.times
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestUpdateCompetition {

    val dataGenerator = DataGenerator()
    private final val mockedRepository = Mockito.mock(ICompetitionRepository::class.java)
    val updateCompetition = UpdateCompetition(mockedRepository)

    @Test
    fun shouldNotStoreUpdateIfEntityIsNotValid() {
        // Setup
        val dto = dataGenerator.newCompetitionDTO(name = "")

        // Act
        Assertions.assertThrows(IllegalArgumentException::class.java) { updateCompetition.execute(dto) }

        // Assert
        Mockito.verify(mockedRepository, times(0)).update(TestHelper.MockitoHelper.anyObject())
    }

    @Test
    fun shouldStoreUpdateIfEntityIsValid() {
        // Setup
        val dto = dataGenerator.newCompetitionDTO()

        // Act
        updateCompetition.execute(dto)

        // Assert
        Mockito.verify(mockedRepository, times(1)).update(dto)
        Mockito.verify(mockedRepository, times(1)).update(TestHelper.MockitoHelper.anyObject())
    }
}