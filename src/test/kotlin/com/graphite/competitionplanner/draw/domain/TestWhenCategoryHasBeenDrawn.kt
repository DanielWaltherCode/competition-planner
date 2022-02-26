package com.graphite.competitionplanner.draw.domain

import com.graphite.competitionplanner.competitioncategory.interfaces.CompetitionCategoryDTO
import com.graphite.competitionplanner.competitioncategory.interfaces.CompetitionCategoryStatus
import com.graphite.competitionplanner.competitioncategory.interfaces.DrawType
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestWhenCategoryHasBeenDrawn : TestBaseCreateDraw() {

    @Test
    fun shouldReturnDrawIfAlreadyDone() {
        // Setup
        val competitionCategory = setupDrawnCategory()

        // Act
        createDraw.execute(competitionCategory.id)

        // Assert
        verify(mockedCompetitionDrawRepository, times(1)).get(competitionCategory.id)
        verify(mockedCompetitionDrawRepository, times(1)).get(Mockito.anyInt())
    }

    private fun setupDrawnCategory(): CompetitionCategoryDTO {
        val competitionCategory = dataGenerator.newCompetitionCategoryDTO(
            id = 11,
            settings = dataGenerator.newGeneralSettingsDTO(
                drawType = DrawType.POOL_AND_CUP,
                playersPerGroup = 4
            ),
            status = CompetitionCategoryStatus.DRAWN.name
        )
        Mockito.`when`(mockedFindCompetitionCategory.byId(competitionCategory.id)).thenReturn(competitionCategory)

        return competitionCategory
    }
}