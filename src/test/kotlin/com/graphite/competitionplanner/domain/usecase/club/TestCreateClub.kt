package com.graphite.competitionplanner.domain.usecase.club

import com.graphite.competitionplanner.club.domain.CreateClub
import com.graphite.competitionplanner.domain.dto.ClubDTO
import com.graphite.competitionplanner.club.domain.interfaces.IClubRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.boot.test.context.SpringBootTest


@SpringBootTest
class TestCreateClub() {

    @Test
    fun shouldCallSaveWhenEntityIsOk() {
        val mockedClubRepository = mock(IClubRepository::class.java)
        val create = CreateClub(mockedClubRepository)
        val dto = ClubDTO(0, "ClubA", "Address1")

        create.execute(dto)

        verify(mockedClubRepository).store(dto)
    }

    @Test
    fun shouldNotSaveWhenEntityIsInvalid() {
        val mockedClubRepository = mock(IClubRepository::class.java)
        val create = CreateClub(mockedClubRepository)
        val invalidDto = ClubDTO(0, "", "Address1")

        Assertions.assertThrows(Exception::class.java) { create.execute(invalidDto) }

        verify(mockedClubRepository, never()).store(invalidDto)
    }

    @Test
    fun shouldNotSaveWhenClubWithSameNameAlreadyExist() {
        val mockedClubRepository = mock(IClubRepository::class.java)
        val create = CreateClub(mockedClubRepository)
        val dto = ClubDTO(0, "ClubA", "Address1")
        val clubWithSameName = ClubDTO(123, dto.name, "Address2")

        `when`(mockedClubRepository.getAll()).thenReturn(listOf(clubWithSameName))

        create.execute(dto)

        verify(mockedClubRepository, never()).store(dto)
    }

}