package com.graphite.competitionplanner.draw.domain

import com.graphite.competitionplanner.competitioncategory.interfaces.DrawType
import com.graphite.competitionplanner.draw.interfaces.ApproveSeedingSpec
import com.graphite.competitionplanner.util.TestHelper
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mockito
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestSeeding: TestBaseCreateDraw() {

    private val inputTestData = listOf(
        DrawType.CUP_ONLY,
        DrawType.POOL_AND_CUP,
        DrawType.POOL_ONLY
    )


    @Captor
    lateinit var seedingCaptor: ArgumentCaptor<ApproveSeedingSpec>

    @TestFactory
    fun shouldApproveSeedingAutomaticallyIfCompetitionIsOpenForRegistration() = inputTestData
        .map { drawType ->
            DynamicTest.dynamicTest("Storing seed when draw type is $drawType") {
            val competitionCategory = dataGenerator.newCompetitionCategoryDTO(
                id = 33,
                settings = dataGenerator.newGeneralSettingsDTO(
                    drawType = drawType,
                    playersPerGroup = 5
                )
            )
            val registrationRanks = (1..5).toList().map {
                dataGenerator.newRegistrationRankDTO(competitionCategoryId = competitionCategory.id, rank = it)
            }
            Mockito.`when`(mockedFindCompetitionCategory.byId(competitionCategory.id)).thenReturn(competitionCategory)
            Mockito.`when`(mockedRegistrationRepository.getRegistrationRanking(competitionCategory))
                .thenReturn(registrationRanks)

            // Act
            createDraw.execute(competitionCategory.id)

            // Record the seeding
            Mockito.verify(mockedApproveSeeding, Mockito.atLeastOnce()).execute(TestHelper.MockitoHelper.anyObject(), TestHelper.MockitoHelper.capture(seedingCaptor))
            val result = seedingCaptor.value as ApproveSeedingSpec

            // Assert
            val storedSeeding = result.seeding
            Assertions.assertEquals(registrationRanks.size, storedSeeding.size)
            Assertions.assertEquals(registrationRanks.map { it.registration.id }.sorted(), storedSeeding.map { it.registration.id }.sorted())
        }
    }
}