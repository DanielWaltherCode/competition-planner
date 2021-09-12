package com.graphite.competitionplanner.competitioncategory.service

import com.graphite.competitionplanner.competitioncategory.domain.*
import com.graphite.competitionplanner.competitioncategory.repository.CompetitionCategoryRepository
import com.graphite.competitionplanner.registration.repository.RegistrationRepository
import com.graphite.competitionplanner.schedule.service.ScheduleService
import com.graphite.competitionplanner.util.DataGenerator
import com.graphite.competitionplanner.util.TestHelper
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestCompetitionCategoryService {

    private final val mockedCompetitionCategoryRepository: CompetitionCategoryRepository =
        mock(CompetitionCategoryRepository::class.java)
    private final val mockedScheduledService: ScheduleService = mock(ScheduleService::class.java)
    private final val mockedRegistrationRepository: RegistrationRepository = mock(RegistrationRepository::class.java)
    private final val mockedAddCompetition: AddCompetitionCategory = mock(AddCompetitionCategory::class.java)
    private final val mockedGetCompetitionCategory: GetCompetitionCategory =
        mock(GetCompetitionCategory::class.java)
    private final val mockedGetCompetitionCategories: GetCompetitionCategories =
        mock(GetCompetitionCategories::class.java)
    private final val mockedUpdateCompetitionCategories: UpdateCompetitionCategory =
        mock(UpdateCompetitionCategory::class.java)
    private final val mockedDeleteCompetitionCategory: DeleteCompetitionCategory =
        mock(DeleteCompetitionCategory::class.java)
    private final val mockedCancelCompetitionCategory: CancelCompetitionCategory =
        mock(CancelCompetitionCategory::class.java)

    val service = CompetitionCategoryService(
        mockedCompetitionCategoryRepository,
        mockedScheduledService,
        mockedRegistrationRepository,
        mockedAddCompetition,
        mockedGetCompetitionCategory,
        mockedGetCompetitionCategories,
        mockedUpdateCompetitionCategories,
        mockedDeleteCompetitionCategory,
        mockedCancelCompetitionCategory
    )

    val dataGenerator = DataGenerator()

    @Test
    fun addingCompetitionCategoryShouldDelegateToUseCase() {
        // Setup
        val competitionId = 1
        val spec = dataGenerator.newCategorySpec()
        val competitionCategory = dataGenerator.newCompetitionCategoryDTO(category = spec)
        // Simulate that we created a competition category and return it
        `when`(mockedAddCompetition.execute(competitionId, spec)).thenReturn(competitionCategory)

        // Act
        val actual = service.addCompetitionCategory(competitionId, spec)

        // Assert
        Assertions.assertEquals(
            competitionCategory, actual,
            "Expected the service to return the newly created competition category."
        )
        verify(mockedAddCompetition, times(1)).execute(competitionId, spec)
        verify(mockedAddCompetition, times(1)).execute(anyInt(), TestHelper.MockitoHelper.anyObject())
    }
}