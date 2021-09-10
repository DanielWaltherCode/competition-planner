package com.graphite.competitionplanner.club.domain

import com.graphite.competitionplanner.club.domain.interfaces.ClubDTO
import com.graphite.competitionplanner.club.domain.interfaces.IClubRepository
import com.graphite.competitionplanner.player.domain.interfaces.IPlayerRepository
import com.graphite.competitionplanner.util.DataGenerator
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestDeleteClub {

    val dataGenerator = DataGenerator()

    private final val mockedClubRepository = mock(IClubRepository::class.java)
    private final val mockedPlayerRepository = mock(IPlayerRepository::class.java)
    val deleteClub = DeleteClub(mockedClubRepository, mockedPlayerRepository)

    @Test
    fun canDeleteClub() {
        // Setup
        val dto = ClubDTO(133, "Silken IK", "Travgatan 37")

        `when`(mockedPlayerRepository.playersInClub(dto)).thenReturn(emptyList())
        `when`(mockedClubRepository.delete(dto)).thenReturn(dto)

        // Act
        val deleted = deleteClub.execute(dto)

        // Assert
        Assertions.assertEquals(dto.id, deleted.id)
        verify(mockedClubRepository, atLeastOnce()).delete(dto)
    }

    @Test
    fun cannotDeleteClubWithPlayers() {
        // Setup
        val dto = ClubDTO(133, "Silken IK", "Travgatan 37")

        `when`(mockedPlayerRepository.playersInClub(dto)).thenReturn(listOf(dataGenerator.newPlayerWithClubDTO(clubDTO = dto)))

        // Act
        Assertions.assertThrows(Exception::class.java) { deleteClub.execute(dto) }
        verify(mockedClubRepository, never()).delete(dto)
    }
}