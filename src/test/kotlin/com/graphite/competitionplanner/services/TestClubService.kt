package com.graphite.competitionplanner.services

import com.graphite.competitionplanner.club.domain.*
import com.graphite.competitionplanner.club.domain.interfaces.ClubDTO
import com.graphite.competitionplanner.club.service.ClubService
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestClubService {

    private final val mockedDeleteUseCase: DeleteClub = Mockito.mock(DeleteClub::class.java)
    private final val mockedCreateUseCase: CreateClub = Mockito.mock(CreateClub::class.java)
    private final val mockedUpdateUseCase: UpdateClub = Mockito.mock(UpdateClub::class.java)
    private final val mockedListAllUseCase: ListAllClubs = Mockito.mock(ListAllClubs::class.java)
    private final val mockedFindClub: FindClub = Mockito.mock(FindClub::class.java)
    val service = ClubService(
        mockedDeleteUseCase,
        mockedCreateUseCase,
        mockedUpdateUseCase,
        mockedListAllUseCase,
        mockedFindClub
    )

    @Test
    fun shouldCallDeleteUseCase() {
        service.delete(10)
        Mockito.verify(mockedDeleteUseCase, Mockito.times(1)).execute(ClubDTO(10, "", ""))
    }

    @Test
    fun shouldCallUpdateUseCase() {
        val dto = ClubDTO(10, "Smicker", "A")

        `when`(mockedUpdateUseCase.execute(dto)).thenReturn(dto)

        val updatedDto = service.updateClub(dto)

        Assertions.assertEquals(dto, updatedDto)
        Mockito.verify(mockedUpdateUseCase, Mockito.times(1)).execute(dto)
    }

    @Test
    fun shouldCallCreateUseCase() {
        val dto = ClubDTO(0, "Smicker", "A")

        `when`(mockedCreateUseCase.execute(dto)).thenReturn(dto)

        val createdDto = service.addClub(dto)

        Assertions.assertEquals(dto, createdDto)
        Mockito.verify(mockedCreateUseCase, Mockito.times(1)).execute(dto)
    }
}