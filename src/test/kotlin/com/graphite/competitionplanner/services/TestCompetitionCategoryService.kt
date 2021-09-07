package com.graphite.competitionplanner.services

import com.graphite.competitionplanner.DataGenerator
import com.graphite.competitionplanner.TestHelper
import com.graphite.competitionplanner.domain.usecase.competition.AddCompetitionCategory
import com.graphite.competitionplanner.domain.usecase.competition.GetCompetitionCategories
import com.graphite.competitionplanner.domain.usecase.competition.UpdateCompetitionCategory
import com.graphite.competitionplanner.repositories.ClubRepository
import com.graphite.competitionplanner.repositories.RegistrationRepository
import com.graphite.competitionplanner.repositories.competition.CompetitionCategoryRepository
import com.graphite.competitionplanner.service.CategoryService
import com.graphite.competitionplanner.service.ScheduleService
import com.graphite.competitionplanner.service.competition.CompetitionCategoryService
import org.jooq.Update
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestCompetitionCategoryService {

    private final val mockedClubRepository: ClubRepository = mock(ClubRepository::class.java)
    private final val mockedCompetitionCategoryRepository: CompetitionCategoryRepository =
        mock(CompetitionCategoryRepository::class.java)
    private final val mockedScheduledService: ScheduleService = mock(ScheduleService::class.java)
    private final val mockedCategoryService: CategoryService = mock(CategoryService::class.java)
    private final val mockedRegistrationRepository: RegistrationRepository = mock(RegistrationRepository::class.java)
    private final val mockedAddCompetition: AddCompetitionCategory = mock(AddCompetitionCategory::class.java)
    private final val mockedGetCompetitionCategories: GetCompetitionCategories =
        mock(GetCompetitionCategories::class.java)
    private final val mockedUpdateCompetitionCategories: UpdateCompetitionCategory =
        mock(UpdateCompetitionCategory::class.java)

    val service = CompetitionCategoryService(
        mockedClubRepository,
        mockedCompetitionCategoryRepository,
        mockedScheduledService,
        mockedCategoryService,
        mockedRegistrationRepository,
        mockedAddCompetition,
        mockedGetCompetitionCategories,
        mockedUpdateCompetitionCategories
    )

    val dataGenerator = DataGenerator()

    @Test
    fun addingCompetitionCategoryShouldDelegateToUseCase() {
        // Setup
        val competitionId = 1
        val competitionCategory = dataGenerator.newCompetitionCategoryDTO()
        // Simulate that we created a competition category and return it
        `when`(mockedAddCompetition.execute(1, competitionCategory.category)).thenReturn(competitionCategory)

        // Act
        val actual = service.addCompetitionCategory(competitionId, competitionCategory.category)

        // Assert
        Assertions.assertEquals(
            competitionCategory, actual,
            "Expected the service to return the newly created competition category."
        )
        verify(mockedAddCompetition, times(1)).execute(competitionId, competitionCategory.category)
        verify(mockedAddCompetition, times(1)).execute(anyInt(), TestHelper.MockitoHelper.anyObject())
    }
}