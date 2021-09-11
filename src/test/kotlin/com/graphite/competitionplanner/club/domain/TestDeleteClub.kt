package com.graphite.competitionplanner.club.domain

import com.graphite.competitionplanner.club.interfaces.ClubDTO
import com.graphite.competitionplanner.club.interfaces.IClubRepository
import com.graphite.competitionplanner.player.interfaces.IPlayerRepository
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

        `when`(mockedPlayerRepository.playersInClub(dto.id)).thenReturn(emptyList())
        `when`(mockedClubRepository.delete(dto.id)).thenReturn(true)

        // Act
        val deleted = deleteClub.execute(dto.id)

        // Assert
        Assertions.assertEquals(true, deleted)
        verify(mockedClubRepository, times(1)).delete(dto.id)
        verify(mockedClubRepository, times(1)).delete(anyInt())
    }

    @Test
    fun cannotDeleteClubWithPlayers() {
        // Setup
        val dto = ClubDTO(133, "Silken IK", "Travgatan 37")

        `when`(mockedPlayerRepository.playersInClub(dto.id)).thenReturn(
            listOf(
                dataGenerator.newPlayerWithClubDTO(
                    clubDTO = dto
                )
            )
        )

        // Act
        Assertions.assertThrows(Exception::class.java) { deleteClub.execute(dto.id) }
        verify(mockedClubRepository, never()).delete(dto.id)
    }
}