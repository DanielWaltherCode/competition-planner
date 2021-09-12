package com.graphite.competitionplanner.competition.domain

import com.graphite.competitionplanner.common.exception.NotFoundException
import com.graphite.competitionplanner.competition.interfaces.ICompetitionRepository
import com.graphite.competitionplanner.util.DataGenerator
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDate

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
        verify(mockedCompetitionRepository, times(1)).findCompetitionsThatBelongsTo(club.id)
        verify(mockedCompetitionRepository, times(1)).findCompetitionsThatBelongsTo(anyInt())
    }

    @Test
    fun shouldNotReturnBYEWhenReturningCompetitionsForClub() {
        // Setup
        val club = dataGenerator.newClubDTO(id = 100)
        val competitions = listOf(
            dataGenerator.newCompetitionDTO(organizingClubId = club.id),
            dataGenerator.newCompetitionDTO(name = "BYE", organizingClubId = club.id)
        )
        `when`(mockedCompetitionRepository.findCompetitionsThatBelongsTo(club.id)).thenReturn(competitions)

        // Act
        val actual = findCompetitions.thatBelongsTo(club.id)

        // Assert
        val byeIsIncluded = actual.any { it.name == "BYE" }
        Assertions.assertFalse(byeIsIncluded, "The default competition BYE was returned.")
    }

    @Test
    fun shouldNotReturnBYEWhenSearchForCompetitions() {
        // Setup
        val start = LocalDate.now()
        val end = start.plusMonths(1)
        val club = dataGenerator.newClubDTO(id = 100)
        val competitions = listOf(
            dataGenerator.newCompetitionWithClubDTO(club = club),
            dataGenerator.newCompetitionWithClubDTO(name = "BYE", club = club)
        )
        `when`(mockedCompetitionRepository.findCompetitions(start, end)).thenReturn(competitions)

        // Act
        val actual = findCompetitions.thatStartOrEndWithin(start, end)

        // Assert
        val byeIsIncluded = actual.any { it.name == "BYE" }
        Assertions.assertFalse(byeIsIncluded, "The default competition BYE was returned.")
    }

    @Test
    fun shouldRaiseNotFoundExceptionWhenAskingForBYECompetition() {
        // Setup
        val competitionId = 0
        val club = dataGenerator.newClubDTO(id = 100)
        val competition = dataGenerator.newCompetitionDTO(name = "BYE", organizingClubId = club.id)

        `when`(mockedCompetitionRepository.findById(competitionId)).thenReturn(competition)

        // Act & Assert
        Assertions.assertThrows(NotFoundException::class.java) {
            findCompetitions.byId(competitionId)
        }
    }
}