package com.graphite.competitionplanner.draw.domain

import com.graphite.competitionplanner.competitioncategory.domain.FindCompetitionCategory
import com.graphite.competitionplanner.competitioncategory.interfaces.ICompetitionCategoryRepository
import com.graphite.competitionplanner.draw.interfaces.ICompetitionDrawRepository
import com.graphite.competitionplanner.registration.interfaces.IRegistrationRepository
import com.graphite.competitionplanner.util.DataGenerator
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mockito
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestBaseCreateDraw {

    protected final val mockedFindCompetitionCategory: FindCompetitionCategory = Mockito.mock(FindCompetitionCategory::class.java)
    protected final val mockedRegistrationRepository: IRegistrationRepository = Mockito.mock(IRegistrationRepository::class.java)
    protected final val mockedCompetitionDrawRepository: ICompetitionDrawRepository = Mockito.mock(
        ICompetitionDrawRepository::class.java)
    protected final val mockedCompetitionCategoryRepository: ICompetitionCategoryRepository = Mockito.mock(ICompetitionCategoryRepository::class.java)

    protected final val dataGenerator = DataGenerator()

    @Captor
    lateinit var classCaptor: ArgumentCaptor<CompetitionCategoryDrawSpec>

    protected val createDraw = CreateDraw(
        mockedFindCompetitionCategory,
        mockedRegistrationRepository,
        mockedCompetitionDrawRepository,
        mockedCompetitionCategoryRepository
    )
}