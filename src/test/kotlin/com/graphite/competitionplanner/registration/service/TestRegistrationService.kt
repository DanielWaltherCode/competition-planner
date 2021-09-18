package com.graphite.competitionplanner.registration.service

import com.graphite.competitionplanner.competition.service.CompetitionService
import com.graphite.competitionplanner.competitioncategory.repository.CompetitionCategoryRepository
import com.graphite.competitionplanner.player.service.PlayerService
import com.graphite.competitionplanner.registration.domain.GetPlayersFromRegistration
import com.graphite.competitionplanner.registration.domain.RegisterDoubleToCompetition
import com.graphite.competitionplanner.registration.domain.RegisterPlayerToCompetition
import com.graphite.competitionplanner.registration.domain.Unregister
import com.graphite.competitionplanner.registration.repository.RegistrationRepository
import com.graphite.competitionplanner.util.DataGenerator
import com.graphite.competitionplanner.util.TestHelper
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestRegistrationService {

    private val mockedRegistrationRepository = mock(RegistrationRepository::class.java)
    private val mockedCompetitionService = mock(CompetitionService::class.java)
    private val mockedPlayerService = mock(PlayerService::class.java)
    private val mockedCompetitionCategoryRepository = mock(CompetitionCategoryRepository::class.java)
    private val mockedRegisterPlayerToCompetition = mock(RegisterPlayerToCompetition::class.java)
    private val mockedRegisterDoublesToCompetition = mock(RegisterDoubleToCompetition::class.java)
    private val mockedUnregister = mock(Unregister::class.java)
    private val mockedGetPlayersFromRegistration = mock(GetPlayersFromRegistration::class.java)
    private val service = RegistrationService(
        mockedRegistrationRepository,
        mockedCompetitionService,
        mockedPlayerService,
        mockedCompetitionCategoryRepository,
        mockedRegisterPlayerToCompetition,
        mockedRegisterDoublesToCompetition,
        mockedUnregister,
        mockedGetPlayersFromRegistration
    )

    private val dataGenerator = DataGenerator()

    @Test
    fun shouldDelegateToUseCaseWhenAddingSinglePlayerRegistration() {
        // Setup
        val spec = dataGenerator.newRegistrationSinglesSpec()
        `when`(mockedRegisterPlayerToCompetition.execute(spec)).thenReturn(dataGenerator.newRegistrationSinglesDTO())

        // Act
        service.registerPlayerSingles(spec)

        // Assert
        verify(mockedRegisterPlayerToCompetition, times(1)).execute(spec)
        verify(mockedRegisterPlayerToCompetition, times(1)).execute(TestHelper.MockitoHelper.anyObject())
    }

    @Test
    fun shouldReturnTheRegistrationId() {
        // Setup
        val spec = dataGenerator.newRegistrationSinglesSpec()
        val expected = dataGenerator.newRegistrationSinglesDTO(id = 1456)
        `when`(mockedRegisterPlayerToCompetition.execute(spec)).thenReturn(expected)

        // Act
        val registrationId = service.registerPlayerSingles(spec)

        // Assertions
        Assertions.assertEquals(expected.id, registrationId)
    }

}