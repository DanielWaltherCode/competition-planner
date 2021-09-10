package com.graphite.competitionplanner.domain.usecase.club

import com.graphite.competitionplanner.club.domain.UpdateClub
import com.graphite.competitionplanner.club.domain.interfaces.ClubDTO
import com.graphite.competitionplanner.club.domain.interfaces.IClubRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.never
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestUpdateClub {

    @Test
    fun canUpdateNameIfNotTaken() {
        val mockedClubRepository = Mockito.mock(IClubRepository::class.java)
        val updateClub = UpdateClub(mockedClubRepository)
        val update = ClubDTO(12, "NewName", "Address1")

        // Simulate that name is not taken by other
        `when`(mockedClubRepository.getAll()).thenReturn(listOf(ClubDTO(1, "ClubA", "Address A")))

        updateClub.execute(update)

        Mockito.verify(mockedClubRepository).update(update)
    }

    @Test
    fun canUpdateAddress() {
        val mockedClubRepository = Mockito.mock(IClubRepository::class.java)
        val updateClub = UpdateClub(mockedClubRepository)
        val original = ClubDTO(12, "OldName", "Address1")
        val update = ClubDTO(12, "OldName", "Address3")

        // Simulate that we found ourself
        `when`(mockedClubRepository.getAll()).thenReturn(listOf(original))

        updateClub.execute(update)

        Mockito.verify(mockedClubRepository).update(update)
    }

    @Test
    fun shouldThrowExceptionWhenClubWithNameAlreadyExist() {
        val mockedClubRepository = Mockito.mock(IClubRepository::class.java)
        val updateClub = UpdateClub(mockedClubRepository)
        val original = ClubDTO(12, "ClubB", "Address1")
        val anotherClubWithSameName = ClubDTO(13, "ClubA", "Address 3")
        val dto = ClubDTO(12, "ClubA", "Address1")

        `when`(mockedClubRepository.getAll()).thenReturn(listOf(original, anotherClubWithSameName))

        Assertions.assertThrows(Exception::class.java) { updateClub.execute(dto) }

        Mockito.verify(mockedClubRepository, never()).update(dto)
    }

    @Test
    fun shouldNotUpdateIfNameIsInvalid() {
        val mockedClubRepository = Mockito.mock(IClubRepository::class.java)
        val updateClub = UpdateClub(mockedClubRepository)
        val dto = ClubDTO(12, "", "Address1")

        Assertions.assertThrows(Exception::class.java) { updateClub.execute(dto) }

        Mockito.verify(mockedClubRepository, never()).update(dto)
    }

}