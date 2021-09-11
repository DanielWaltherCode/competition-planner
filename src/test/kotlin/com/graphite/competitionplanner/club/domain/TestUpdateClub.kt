package com.graphite.competitionplanner.club.domain

import com.graphite.competitionplanner.club.interfaces.ClubDTO
import com.graphite.competitionplanner.club.interfaces.IClubRepository
import com.graphite.competitionplanner.util.DataGenerator
import com.graphite.competitionplanner.util.TestHelper
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestUpdateClub {

    private final val mockedClubRepository = mock(IClubRepository::class.java)
    val updateClub = UpdateClub(mockedClubRepository)
    val dataGenerator = DataGenerator()

    @Test
    fun canUpdateNameIfNotTaken() {
        val spec = dataGenerator.newClubSpec("NewName", "Address1")

        // Simulate that name is not taken by other
        `when`(mockedClubRepository.getAll()).thenReturn(listOf(ClubDTO(1, "ClubA", "Address A")))

        updateClub.execute(12, spec)

        verify(mockedClubRepository, times(1)).update(12, spec)
        verify(mockedClubRepository, times(1)).update(anyInt(), TestHelper.MockitoHelper.anyObject())
    }

    @Test
    fun canUpdateAddress() {
        val original = ClubDTO(12, "OldName", "Address1")
        val spec = dataGenerator.newClubSpec("OldName", "Address3")

        // Simulate that we found ourself
        `when`(mockedClubRepository.getAll()).thenReturn(listOf(original))

        updateClub.execute(12, spec)

        verify(mockedClubRepository, times(1)).update(12, spec)
        verify(mockedClubRepository, times(1)).update(anyInt(), TestHelper.MockitoHelper.anyObject())
    }

    @Test
    fun shouldThrowExceptionWhenClubWithNameAlreadyExist() {
        val original = ClubDTO(12, "ClubB", "Address1")
        val anotherClubWithSameName = ClubDTO(13, "ClubA", "Address 3")
        val spec = dataGenerator.newClubSpec("ClubA", "Address1")

        `when`(mockedClubRepository.getAll()).thenReturn(listOf(original, anotherClubWithSameName))

        Assertions.assertThrows(Exception::class.java) { updateClub.execute(12, spec) }

        verify(mockedClubRepository, never()).update(anyInt(), TestHelper.MockitoHelper.anyObject())
    }

}