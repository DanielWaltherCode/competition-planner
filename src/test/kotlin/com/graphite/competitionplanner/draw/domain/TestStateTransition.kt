package com.graphite.competitionplanner.draw.domain

import com.graphite.competitionplanner.competitioncategory.domain.FindCompetitionCategory
import com.graphite.competitionplanner.competitioncategory.interfaces.CompetitionCategoryDTO
import com.graphite.competitionplanner.competitioncategory.interfaces.CompetitionCategoryStatus
import com.graphite.competitionplanner.competitioncategory.interfaces.DrawType
import com.graphite.competitionplanner.competitioncategory.interfaces.ICompetitionCategoryRepository
import com.graphite.competitionplanner.draw.interfaces.ICompetitionDrawRepository
import com.graphite.competitionplanner.draw.interfaces.ISeedRepository
import com.graphite.competitionplanner.registration.domain.GetRegistrationsInCompetitionCategory
import com.graphite.competitionplanner.registration.interfaces.IRegistrationRepository
import com.graphite.competitionplanner.util.DataGenerator
import com.graphite.competitionplanner.util.TestHelper
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.kotlin.inOrder
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestStateTransition {

    private val mockedGetRegistrationInCompetitionCategory =
        Mockito.mock(GetRegistrationsInCompetitionCategory::class.java)
    private val mockedFindCompetitionCategory = Mockito.mock(FindCompetitionCategory::class.java)
    private val mockedRegistrationRepository = Mockito.mock(IRegistrationRepository::class.java)
    private val mockedSeedRepository = Mockito.mock(ISeedRepository::class.java)
    private val mockedCompetitionDrawRepository = Mockito.mock(ICompetitionDrawRepository::class.java)
    private val mockedCompetitionCategoryRepository = Mockito.mock(ICompetitionCategoryRepository::class.java)

    private val createDraw = CreateDraw(
        mockedGetRegistrationInCompetitionCategory,
        mockedFindCompetitionCategory,
        CreateSeed(),
        mockedRegistrationRepository,
        mockedSeedRepository,
        mockedCompetitionDrawRepository,
        mockedCompetitionCategoryRepository
    )

    private val dataGenerator = DataGenerator()

    @Test
    fun shouldSetStatusToDrawn() {
        // Setup
        val competitionCategory = setupCompetitionCategory()

        // Act
        createDraw.execute(competitionCategory.id)

        // Assert
        verify(mockedCompetitionCategoryRepository, times(1)).setStatus(competitionCategory.id, CompetitionCategoryStatus.DRAWN)
        verify(mockedCompetitionCategoryRepository, times(1)).setStatus(Mockito.anyInt(), TestHelper.MockitoHelper.anyObject())
    }

    @Test
    fun mustSetStatusBeforeReadingSettings() {
        // Setup
        val competitionCategory = setupCompetitionCategory()

        // Act
        createDraw.execute(competitionCategory.id)

        // Assert
        val checkOrder = inOrder(mockedCompetitionCategoryRepository, mockedFindCompetitionCategory)
        checkOrder.verify(mockedCompetitionCategoryRepository).setStatus(Mockito.anyInt(), TestHelper.MockitoHelper.anyObject())
        checkOrder.verify(mockedFindCompetitionCategory).byId(Mockito.anyInt())
    }

    private fun setupCompetitionCategory(): CompetitionCategoryDTO {
        val competitionCategory = dataGenerator.newCompetitionCategoryDTO(
            id = 11,
            settings = dataGenerator.newGeneralSettingsSpec(
                drawType = DrawType.POOL_AND_CUP,
                playersPerGroup = 4
            )
        )
        val registrationRanks = (1..10).toList().map {
            dataGenerator.newRegistrationRankDTO(competitionCategoryId = competitionCategory.id, rank = it)
        }
        Mockito.`when`(mockedFindCompetitionCategory.byId(competitionCategory.id)).thenReturn(competitionCategory)
        Mockito.`when`(mockedRegistrationRepository.getRegistrationRanking(competitionCategory)).thenReturn(registrationRanks)

        return competitionCategory
    }
}