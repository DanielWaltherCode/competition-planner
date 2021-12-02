package com.graphite.competitionplanner.draw.domain

import com.graphite.competitionplanner.competitioncategory.interfaces.DrawType
import com.graphite.competitionplanner.competitioncategory.interfaces.ICompetitionCategoryRepository
import com.graphite.competitionplanner.competitioncategory.entity.Round
import com.graphite.competitionplanner.draw.interfaces.CompetitionCategoryDrawDTO
import com.graphite.competitionplanner.draw.interfaces.GroupDrawDTO
import com.graphite.competitionplanner.draw.interfaces.ICompetitionDrawRepository
import com.graphite.competitionplanner.draw.interfaces.PlayOffMatchDTO
import com.graphite.competitionplanner.util.DataGenerator
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.anyInt
import org.mockito.Mockito.times
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestGetDraw {

    private val mockedDrawRepository = Mockito.mock(ICompetitionDrawRepository::class.java)
    private val mockedCompetitionCategoryRepository = Mockito.mock(ICompetitionCategoryRepository::class.java)
    private val getDraw = GetDraw(mockedDrawRepository, mockedCompetitionCategoryRepository)

    val dataGenerator = DataGenerator()

    @Test
    fun shouldValidateThatCompetitionCategoryExist() {
        // Setup
        val competitionCategoryId = 3345

        // Act
        getDraw.execute(competitionCategoryId)

        // Assert
        Mockito.verify(mockedCompetitionCategoryRepository, times(1)).get(competitionCategoryId)
        Mockito.verify(mockedCompetitionCategoryRepository, times(1)).get(anyInt())
    }

    @Test
    fun shouldDelegateToRepository() {
        // Setup
        val competitionCategoryId = 5555

        // Act
        getDraw.execute(competitionCategoryId)

        // Assert
        Mockito.verify(mockedDrawRepository, times(1)).get(competitionCategoryId)
        Mockito.verify(mockedDrawRepository, times(1)).get(anyInt())
    }

    @Test
    fun shouldMapDisplayNameOfPlaceholderMatches() {
        // Setup
        val competitionCategory = dataGenerator.newCompetitionCategoryDTO(
            id = 15236,
            settings = dataGenerator.newGeneralSettingsSpec(
                drawType = DrawType.POOL_AND_CUP,
                playersPerGroup = 4,
                playersToPlayOff = 2
            )
        )

        val drawDto = CompetitionCategoryDrawDTO(
            competitionCategory.id,
            playOff = listOf(
                newPlaceholderMatch(Round.SEMI_FINAL, 1),
                newPlaceholderMatch(Round.SEMI_FINAL, 2),
                newPlaceholderMatch(Round.FINAL, 1)
            ),
            groupDraw = listOf(
                newGroupDrawDtoWithPlayerNames("A", listOf("Steeve", "Charles", "Klark", "Kent")),
                newGroupDrawDtoWithPlayerNames("B", listOf("Steeve2", "Charles2", "Klark2", "Kent2"))
            )
        )

        Mockito.`when`(mockedCompetitionCategoryRepository.get(competitionCategory.id)).thenReturn(competitionCategory)
        Mockito.`when`(mockedDrawRepository.get(competitionCategory.id)).thenReturn(drawDto)

        // Act
        val result = getDraw.execute(competitionCategory.id)

        // Act
        Assertions.assertTrue(true)
    }

    private fun newPlaceholderMatch(round: Round, order: Int): PlayOffMatchDTO {
        return dataGenerator.newPlayOffMatchDto(
            player1 = listOf(dataGenerator.newPlayerWithClubDTO(firstName = "Placeholder", lastName = "Placeholder")),
            player2 = listOf(dataGenerator.newPlayerWithClubDTO(firstName = "Placeholder", lastName = "Placeholder")),
            round = round,
            order = order
        )
    }

    private fun newGroupDrawDtoWithPlayerNames(groupName: String, names: List<String>): GroupDrawDTO {
        return GroupDrawDTO(
            groupName,
            names.map { dataGenerator.newPlayerWithClubDTO(firstName = it) },
            emptyList(),
        )
    }
}