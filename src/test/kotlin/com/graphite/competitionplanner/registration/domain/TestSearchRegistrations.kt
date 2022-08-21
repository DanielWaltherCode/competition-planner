package com.graphite.competitionplanner.registration.domain

import com.graphite.competitionplanner.category.domain.DefaultCategory
import com.graphite.competitionplanner.registration.interfaces.IRegistrationRepository
import com.graphite.competitionplanner.registration.service.SearchType
import com.graphite.competitionplanner.util.DataGenerator
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestSearchRegistrations {

    private val mockedRegistrationRepository = mock(IRegistrationRepository::class.java)
    private val searchRegistrations = SearchRegistrations(mockedRegistrationRepository)
    private val dataGenerator = DataGenerator()

    @Test
    fun resultShouldSpecifyThatItHasBeenGroupedByName() {
        // Setup
        val expectedType = SearchType.NAME

        // Act
        val result = searchRegistrations.execute(dataGenerator.newCompetitionDTO(), expectedType)

        // Assert
        Assertions.assertEquals(expectedType, result.groupingType)
    }

    @Test
    fun resultShouldSpecifyThatItHasBeenGroupedByCategory() {
        // Setup
        val expectedType = SearchType.CATEGORY

        // Act
        val result = searchRegistrations.execute(dataGenerator.newCompetitionDTO(), expectedType)

        // Assert
        Assertions.assertEquals(expectedType, result.groupingType)
    }

    @Test
    fun resultShouldSpecifyThatItHasBeenGroupedByClub() {
        // Setup
        val expectedType = SearchType.CLUB

        // Act
        val result = searchRegistrations.execute(dataGenerator.newCompetitionDTO(), expectedType)

        // Assert
        Assertions.assertEquals(expectedType, result.groupingType)
    }

    @Test
    fun resultShouldBeGroupedByLastName() {
        // Setup
        val playerA = dataGenerator.newPlayerWithClubDTO(lastName = "A")
        val playerAB = dataGenerator.newPlayerWithClubDTO(lastName = "AB")
        val playerC = dataGenerator.newPlayerWithClubDTO(lastName = "C")
        val playerD = dataGenerator.newPlayerWithClubDTO(lastName = "D")
        val players = listOf(playerA, playerC, playerD, playerAB)
        val competition = dataGenerator.newCompetitionDTO()
        `when`(mockedRegistrationRepository.getAllRegisteredPlayersInCompetition(competition.id)).thenReturn(players)

        // Act
        val result = searchRegistrations.execute(dataGenerator.newCompetitionDTO(), SearchType.NAME)

        // Assert
        val expectedGroups = listOf("A", "C", "D")
        val actualGroups = result.groupingsAndPlayers.map { it.key }
        Assertions.assertEquals(expectedGroups, actualGroups, "Missing groups or result is not sorted in alphanumeric order")

        val playersOnA = result.groupingsAndPlayers["A"]
        Assertions.assertTrue(playersOnA!!.contains(playerA))
        Assertions.assertTrue(playersOnA.contains(playerAB))

        val playersOnC = result.groupingsAndPlayers["C"]
        Assertions.assertTrue(playersOnC!!.contains(playerC))

        val playersOnD = result.groupingsAndPlayers["D"]
        Assertions.assertTrue(playersOnD!!.contains(playerD))
    }

    @Test
    fun resultShouldBeGroupedByClubName() {
        // Setup
        val clubA = dataGenerator.newClubDTO(name = "Alle")
        val clubF = dataGenerator.newClubDTO(name = "Fishers")
        val player1A = dataGenerator.newPlayerWithClubDTO(clubDTO = clubA)
        val player2A = dataGenerator.newPlayerWithClubDTO(clubDTO = clubA)
        val player3A = dataGenerator.newPlayerWithClubDTO(clubDTO = clubA)
        val player1F = dataGenerator.newPlayerWithClubDTO(clubDTO = clubF)
        val players = listOf(player2A, player1F, player3A, player1A)
        val competition = dataGenerator.newCompetitionDTO()
        `when`(mockedRegistrationRepository.getAllRegisteredPlayersInCompetition(competition.id)).thenReturn(players)

        // Act
        val result = searchRegistrations.execute(dataGenerator.newCompetitionDTO(), SearchType.CLUB)

        // Assert
        val expectedGroups = listOf(clubA.name, clubF.name)
        val actualGroups = result.groupingsAndPlayers.map { it.key }
        Assertions.assertEquals(expectedGroups, actualGroups, "Missing groups or result is not sorted in alphanumeric order")

        val playersInClubA = result.groupingsAndPlayers[clubA.name]
        Assertions.assertTrue(playersInClubA!!.contains(player1A))
        Assertions.assertTrue(playersInClubA.contains(player2A))
        Assertions.assertTrue(playersInClubA.contains(player3A))

        val playersInClubF = result.groupingsAndPlayers[clubF.name]
        Assertions.assertTrue(playersInClubF!!.contains(player1F))
    }

    @Test
    fun resultShouldBeGroupedByCategoryName() {
        // Setup
        val categoryOne = dataGenerator.newCategoryDTO(name = DefaultCategory.MEN_1.name)
        val categoryTwo = dataGenerator.newCategoryDTO(name = DefaultCategory.WOMEN_1.name)
        val player1 = dataGenerator.newPlayerWithClubDTO()
        val player2 = dataGenerator.newPlayerWithClubDTO()
        val player3 = dataGenerator.newPlayerWithClubDTO()
        val player4 = dataGenerator.newPlayerWithClubDTO()
        val categoriesAndPlayers = listOf(
            Pair(categoryOne, player1),
            Pair(categoryOne, player2),
            Pair(categoryOne, player3),
            Pair(categoryTwo, player1),
            Pair(categoryTwo, player4),
        )
        val competition = dataGenerator.newCompetitionDTO()
        `when`(mockedRegistrationRepository.getCategoriesAndPlayersInCompetition(competition.id)).thenReturn(categoriesAndPlayers)

        // Act
        val result = searchRegistrations.execute(dataGenerator.newCompetitionDTO(), SearchType.CATEGORY)

        // Assert
        val expectedGroups = listOf(categoryOne.name, categoryTwo.name)
        val actualGroups = result.groupingsAndPlayers.map { it.key }
        Assertions.assertEquals(expectedGroups, actualGroups, "Missing groups or result is not sorted in alphanumeric order")

        val playersInCategoryOne = result.groupingsAndPlayers[categoryOne.name]
        Assertions.assertTrue(playersInCategoryOne!!.contains(player1))
        Assertions.assertTrue(playersInCategoryOne.contains(player2))
        Assertions.assertTrue(playersInCategoryOne.contains(player3))

        val playersInCategoryTwo = result.groupingsAndPlayers[categoryTwo.name]
        Assertions.assertTrue(playersInCategoryTwo!!.contains(player1))
        Assertions.assertTrue(playersInCategoryTwo.contains(player4))
    }

    @Test
    fun shouldFilterOutByePlayerWhenGroupingByName() {
        // Setup
        val playerBye = dataGenerator.newPlayerWithClubDTO(lastName = "BYE")
        val playerAB = dataGenerator.newPlayerWithClubDTO(lastName = "AB")
        val playerC = dataGenerator.newPlayerWithClubDTO(lastName = "C")
        val playerD = dataGenerator.newPlayerWithClubDTO(lastName = "D")
        val players = listOf(playerBye, playerC, playerD, playerAB)
        val competition = dataGenerator.newCompetitionDTO()
        `when`(mockedRegistrationRepository.getAllRegisteredPlayersInCompetition(competition.id)).thenReturn(players)

        // Act
        val result = searchRegistrations.execute(dataGenerator.newCompetitionDTO(), SearchType.NAME)

        // Assert
        val actualPlayers = result.groupingsAndPlayers.flatMap { it.value.map { player -> player } }
        Assertions.assertFalse(actualPlayers.contains(playerBye), "BYE player was returned in search result. It should not be the case.")
    }

    @Test
    fun shouldFilterOutByePlayerWhenGroupingByClub() {
        val clubA = dataGenerator.newClubDTO(name = "Alle")
        val clubF = dataGenerator.newClubDTO(name = "Fishers")
        val playerBye = dataGenerator.newPlayerWithClubDTO(lastName = "BYE", clubDTO = clubA)
        val player2A = dataGenerator.newPlayerWithClubDTO(clubDTO = clubA)
        val player3A = dataGenerator.newPlayerWithClubDTO(clubDTO = clubA)
        val player1F = dataGenerator.newPlayerWithClubDTO(clubDTO = clubF)
        val players = listOf(player2A, player1F, player3A, playerBye)
        val competition = dataGenerator.newCompetitionDTO()
        `when`(mockedRegistrationRepository.getAllRegisteredPlayersInCompetition(competition.id)).thenReturn(players)

        // Act
        val result = searchRegistrations.execute(dataGenerator.newCompetitionDTO(), SearchType.CLUB)

        // Assert
        val actualPlayers = result.groupingsAndPlayers.flatMap { it.value.map { player -> player } }
        Assertions.assertFalse(actualPlayers.contains(playerBye), "BYE player was returned in search result. It should not be the case.")
    }

    @Test
    fun shouldFilterOutByePlayerWhenGroupingByCategory() {
        // Setup
        val categoryOne = dataGenerator.newCategoryDTO(name = DefaultCategory.MEN_1.name)
        val categoryTwo = dataGenerator.newCategoryDTO(name = DefaultCategory.WOMEN_1.name)
        val playerBye = dataGenerator.newPlayerWithClubDTO(lastName = "BYE")
        val player2 = dataGenerator.newPlayerWithClubDTO()
        val player3 = dataGenerator.newPlayerWithClubDTO()
        val player4 = dataGenerator.newPlayerWithClubDTO()
        val categoriesAndPlayers = listOf(
            Pair(categoryOne, playerBye),
            Pair(categoryOne, player2),
            Pair(categoryOne, player3),
            Pair(categoryTwo, playerBye),
            Pair(categoryTwo, player4),
        )
        val competition = dataGenerator.newCompetitionDTO()
        `when`(mockedRegistrationRepository.getCategoriesAndPlayersInCompetition(competition.id)).thenReturn(categoriesAndPlayers)

        // Act
        val result = searchRegistrations.execute(dataGenerator.newCompetitionDTO(), SearchType.CATEGORY)

        // Assert
        val actualPlayers = result.groupingsAndPlayers.flatMap { it.value.map { player -> player } }
        Assertions.assertFalse(actualPlayers.contains(playerBye), "BYE player was returned in search result. It should not be the case.")
    }
}

