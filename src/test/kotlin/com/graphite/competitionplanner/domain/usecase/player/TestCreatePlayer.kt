package com.graphite.competitionplanner.domain.usecase.player

import com.graphite.competitionplanner.DataGenerator
import com.graphite.competitionplanner.TestHelper
import com.graphite.competitionplanner.domain.dto.ClubDTO
import com.graphite.competitionplanner.domain.dto.NewPlayerDTO
import com.graphite.competitionplanner.domain.dto.PlayerDTO
import com.graphite.competitionplanner.domain.interfaces.IPlayerRepository
import com.graphite.competitionplanner.domain.interfaces.NotFoundException
import com.graphite.competitionplanner.domain.usecase.club.FindClub
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.boot.test.context.SpringBootTest
import java.lang.IllegalArgumentException
import java.time.LocalDate

@SpringBootTest
class TestCreatePlayer {

    val dataGenerator = DataGenerator()

    private final val mockedPlayerRepository = mock(IPlayerRepository::class.java)
    private final val mockedFindClub = mock(FindClub::class.java)
    private val createPlayer = CreatePlayer(mockedPlayerRepository, mockedFindClub)

    @Test
    fun shouldCallStoreWhenEntityIsOk() {
        // Setup
        val dto = dataGenerator.newNewPlayerDTO()
        `when`(mockedFindClub.byId(dto.clubId)).thenReturn(ClubDTO(dto.clubId, "Liljan IF", "Stovägen"))
        `when`(mockedPlayerRepository.store(TestHelper.MockitoHelper.anyObject())).thenReturn(
            PlayerDTO(
                1,
                dto.firstName,
                dto.lastName,
                dto.clubId,
                dto.dateOfBirth
            )
        )

        // Act
        createPlayer.execute(dto)

        // Assert
        verify(mockedPlayerRepository, times(1)).store(TestHelper.MockitoHelper.anyObject())
    }

    @Test
    fun shouldNotCallStoreWhenEntityIsInvalid() {
        // Assert
        val club = dataGenerator.newClubDTO()
        val dto = NewPlayerDTO("", "lastName", club.id, LocalDate.of(1990, 5, 18))

        // Act
        Assertions.assertThrows(IllegalArgumentException::class.java) { createPlayer.execute(dto) }

        // Assert
        verify(mockedPlayerRepository, never()).store(TestHelper.MockitoHelper.anyObject())
    }

    @Test
    fun shouldAssertThatClubExist() {
        // Setup
        val dto = dataGenerator.newNewPlayerDTO()
        `when`(mockedFindClub.byId(dto.clubId)).thenReturn(ClubDTO(dto.clubId, "Liljan IF", "Stovägen"))
        `when`(mockedPlayerRepository.store(TestHelper.MockitoHelper.anyObject())).thenReturn(
            PlayerDTO(
                1,
                dto.firstName,
                dto.lastName,
                dto.clubId,
                dto.dateOfBirth
            )
        )

        // Act
        createPlayer.execute(dto)

        // Assert
        verify(mockedFindClub, atLeastOnce()).byId(dto.clubId)
    }

    @Test
    fun shouldNotCallStoreUnlessClubHasBeenVerified() {
        // Setup
        val dto = dataGenerator.newNewPlayerDTO()
        `when`(mockedFindClub.byId(dto.clubId)).thenThrow(NotFoundException(""))

        // Act
        Assertions.assertThrows(NotFoundException::class.java) { createPlayer.execute(dto) }

        // Assert
        verify(mockedPlayerRepository, never()).store(TestHelper.MockitoHelper.anyObject())
    }
}