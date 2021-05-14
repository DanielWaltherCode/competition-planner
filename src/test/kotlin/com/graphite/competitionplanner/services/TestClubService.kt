package com.graphite.competitionplanner.services

import com.graphite.competitionplanner.domain.dto.ClubDTO
import com.graphite.competitionplanner.domain.usecase.club.*
import com.graphite.competitionplanner.repositories.ClubRepository
import com.graphite.competitionplanner.service.ClubService
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestClubService {

    @Test
    fun shouldCallDeleteUseCase() {
        val mockedDeleteUseCase = Mockito.mock(DeleteClub::class.java)
        val mockedCreateUseCase = Mockito.mock(CreateClub::class.java)
        val mockedUpdateUseCase = Mockito.mock(UpdateClub::class.java)
        val mockedListAllUseCase = Mockito.mock(ListAllClubs::class.java)
        val mockedFindClub = Mockito.mock(FindClub::class.java)
        val service = ClubService(
            mockedDeleteUseCase,
            mockedCreateUseCase,
            mockedUpdateUseCase,
            mockedListAllUseCase,
            mockedFindClub
        )

        service.delete(10);

        Mockito.verify(mockedDeleteUseCase, Mockito.times(1)).execute(ClubDTO(10, "", ""))
    }

    @Test
    fun shouldCallUpdateUseCase() {
        val mockedDeleteUseCase = Mockito.mock(DeleteClub::class.java)
        val mockedCreateUseCase = Mockito.mock(CreateClub::class.java)
        val mockedUpdateUseCase = Mockito.mock(UpdateClub::class.java)
        val mockedListAllUseCase = Mockito.mock(ListAllClubs::class.java)
        val mockedFindClub = Mockito.mock(FindClub::class.java)
        val service = ClubService(
            mockedDeleteUseCase,
            mockedCreateUseCase,
            mockedUpdateUseCase,
            mockedListAllUseCase,
            mockedFindClub
        )

        val dto = ClubDTO(10, "Smicker", "A")
        val legacyDto = com.graphite.competitionplanner.api.ClubDTO(dto.id, dto.name, dto.address)

        `when`(mockedUpdateUseCase.execute(dto)).thenReturn(dto)

        val updatedDto = service.updateClub(legacyDto);

        Assertions.assertEquals(legacyDto, updatedDto)
        Mockito.verify(mockedUpdateUseCase, Mockito.times(1)).execute(dto)
    }
}