package com.graphite.competitionplanner.competition.domain

import com.graphite.competitionplanner.club.domain.FindClub
import com.graphite.competitionplanner.common.exception.NotFoundException
import com.graphite.competitionplanner.competition.domain.interfaces.ICompetitionRepository
import com.graphite.competitionplanner.util.DataGenerator
import com.graphite.competitionplanner.util.TestHelper
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestCreateCompetition {

    val dataGenerator = DataGenerator()
    private final val mockedFindClub = mock(FindClub::class.java)
    private final val mockedRepository = mock(ICompetitionRepository::class.java)
    val createCompetition = CreateCompetition(mockedRepository, mockedFindClub)

    @Test
    fun shouldStoreCompetitionIfEntityIsOk() {
        // Setup
        val dto = dataGenerator.newNewCompetitionDTO()
        `when`(mockedFindClub.byId(dto.organizingClubId))
            .thenReturn(dataGenerator.newClubDTO(id = dto.organizingClubId))
        `when`(mockedRepository.store(TestHelper.MockitoHelper.anyObject())).thenReturn(
            dataGenerator.newCompetitionDTO(id = 1, organizingClubId = dto.organizingClubId)
        )

        // Act
        createCompetition.execute(dto)

        // Assert
        verify(mockedRepository, times(1)).store(TestHelper.MockitoHelper.anyObject())
    }

    @Test
    fun shouldNotStoreEntityIfItIsInvalid() {
        // Setup
        val dto = dataGenerator.newNewCompetitionDTO(name = "")

        // Act
        Assertions.assertThrows(IllegalArgumentException::class.java) { createCompetition.execute(dto) }

        // Assert
        verify(mockedRepository, never()).store(TestHelper.MockitoHelper.anyObject())
    }

    @Test
    fun shouldAssertThatClubExist() {
        // Setup
        val dto = dataGenerator.newNewCompetitionDTO()
        `when`(mockedFindClub.byId(dto.organizingClubId))
            .thenReturn(dataGenerator.newClubDTO(id = dto.organizingClubId))
        `when`(mockedRepository.store(TestHelper.MockitoHelper.anyObject())).thenReturn(
            dataGenerator.newCompetitionDTO(id = 1, organizingClubId = dto.organizingClubId)
        )

        // Act
        createCompetition.execute(dto)

        // Assert
        verify(mockedFindClub, atLeastOnce()).byId(dto.organizingClubId)
    }

    @Test
    fun shouldNotStoreCompetitionIfClubDoesNotExist() {
        // Setup
        val dto = dataGenerator.newNewCompetitionDTO()
        `when`(mockedFindClub.byId(dto.organizingClubId)).thenThrow(NotFoundException(""))

        // Act
        Assertions.assertThrows(NotFoundException::class.java) { createCompetition.execute(dto) }

        // Assert
        verify(mockedRepository, never()).store(TestHelper.MockitoHelper.anyObject())
    }
}