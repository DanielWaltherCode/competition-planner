package com.graphite.competitionplanner.draw.domain

import com.graphite.competitionplanner.competitioncategory.interfaces.DrawType
import com.graphite.competitionplanner.util.TestHelper
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import org.mockito.Mockito
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestSeeding: TestBaseCreateDraw() {

    private val inputTestData = listOf(
        DrawType.CUP_ONLY,
        DrawType.POOL_AND_CUP,
        DrawType.POOL_ONLY
    )


    @TestFactory
    fun shouldStoreSeeding() = inputTestData
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

            // Record the spec sent to the repository for validation
            Mockito.verify(mockedCompetitionDrawRepository, Mockito.atLeastOnce()).store(TestHelper.MockitoHelper.capture(classCaptor))
            val result = classCaptor.value as CompetitionCategoryDrawSpec

            // Assert
            val seeding = result.seeding
            Assertions.assertEquals(registrationRanks.size, seeding.size)
            Assertions.assertEquals(registrationRanks.map { it.registration.id }.sorted(), seeding.map { it.registrationId }.sorted())
        }
    }
}