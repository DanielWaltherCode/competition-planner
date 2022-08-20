package com.graphite.competitionplanner.registration.service

import com.graphite.competitionplanner.competition.domain.FindCompetitions
import com.graphite.competitionplanner.competitioncategory.repository.CompetitionCategoryRepository
import com.graphite.competitionplanner.registration.domain.Registration
import com.graphite.competitionplanner.match.service.MatchService
import com.graphite.competitionplanner.player.domain.FindPlayer
import com.graphite.competitionplanner.registration.domain.*
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
    private val mockedCompetitionCategoryRepository = mock(CompetitionCategoryRepository::class.java)
    private val mockedRegisterPlayerToCompetition = mock(RegisterPlayerToCompetition::class.java)
    private val mockedRegisterDoublesToCompetition = mock(RegisterDoubleToCompetition::class.java)
    private val mockedUnregister = mock(Unregister::class.java)
    private val mockedGetPlayersFromRegistration = mock(GetPlayersFromRegistration::class.java)
    private val mockedFindPlayer = mock(FindPlayer::class.java)
    private val mockedMatchService = mock(MatchService::class.java)
    private val mockedSearchRegistrations = mock(SearchRegistrations::class.java)
    private val mockedFindCompetitions = mock(FindCompetitions::class.java)
    private val service = RegistrationService(
        mockedRegistrationRepository,
        mockedCompetitionCategoryRepository,
        mockedRegisterPlayerToCompetition,
        mockedRegisterDoublesToCompetition,
        mockedUnregister,
        mockedGetPlayersFromRegistration,
        mockedFindPlayer,
        mockedMatchService,
        mockedSearchRegistrations,
        mockedFindCompetitions
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
    fun shouldReturnCorrectRegistrationId() {
        // Setup
        val spec = dataGenerator.newRegistrationSinglesSpec()
        val expected = dataGenerator.newRegistrationSinglesDTO(id = 1456)
        `when`(mockedRegisterPlayerToCompetition.execute(spec)).thenReturn(expected)

        // Act
        val registration = service.registerPlayerSingles(spec)

        // Assertions
        Assertions.assertEquals(expected.id, registration.id)
    }

    @Test
    fun getPlayersInCompetitionCategoryShouldGroupByRegistrationId() {
        // Setup
        val competitionCategory = dataGenerator.newCompetitionCategoryDTO()
        val fakeResult = listOf(
            Pair(Registration.Real(1022), dataGenerator.newPlayerWithClubDTO(id = 334)),
            Pair(Registration.Real(1022), dataGenerator.newPlayerWithClubDTO(id = 335)),
            Pair(Registration.Real(1033), dataGenerator.newPlayerWithClubDTO(id = 444)),
            Pair(Registration.Bye, dataGenerator.newPlayerWithClubDTO(id = Registration.Bye.asInt())),
            Pair(Registration.Placeholder(), dataGenerator.newPlayerWithClubDTO(id = Registration.Placeholder().asInt()))
        )
        `when`(mockedRegistrationRepository.getAllRegisteredPlayersInCompetitionCategory(competitionCategory.id)).thenReturn(
            fakeResult
        )

        // Act
        val players = service.getPlayersInCompetitionCategory(competitionCategory.id)

        // Assert
        Assertions.assertEquals(2, players.size, "Wrong size. Expected two registrations.")
        Assertions.assertEquals(
            1,
            players.filter { it.size == 2 }.size,
            "Expected to find exactly one double registration."
        )
        Assertions.assertEquals(
            1,
            players.filter { it.size == 1 }.size,
            "Expected to find exactly one single registration"
        )
    }

}