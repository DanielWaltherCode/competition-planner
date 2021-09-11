package com.graphite.competitionplanner.club.service

import com.graphite.competitionplanner.club.domain.*
import com.graphite.competitionplanner.util.DataGenerator
import com.graphite.competitionplanner.util.TestHelper
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.anyInt
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

    val dataGenerator = DataGenerator()

    @Test
    fun shouldCallDeleteUseCase() {
        service.delete(10)
        Mockito.verify(mockedDeleteUseCase, Mockito.times(1)).execute(10)
    }

    @Test
    fun shouldCallUpdateUseCase() {
        // Setup
        val clubId = 10
        val spec = dataGenerator.newClubSpec("Smicker", "A")
        val dto = dataGenerator.newClubDTO(clubId, spec.name, spec.address)
        `when`(mockedUpdateUseCase.execute(clubId, spec)).thenReturn(dto)

        // Act
        val updatedDto = service.updateClub(clubId, spec)

        // Assert
        Assertions.assertEquals(dto, updatedDto)
        Mockito.verify(mockedUpdateUseCase, Mockito.times(1)).execute(10, spec)
        Mockito.verify(mockedUpdateUseCase, Mockito.times(1)).execute(anyInt(), TestHelper.MockitoHelper.anyObject())
    }

    @Test
    fun shouldCallCreateUseCase() {
        // Setup
        val clubId = 134
        val spec = dataGenerator.newClubSpec("Smicker", "A")
        val dto = dataGenerator.newClubDTO(clubId, spec.name, spec.address)
        `when`(mockedCreateUseCase.execute(spec)).thenReturn(dto)

        // Act
        val createdDto = service.addClub(spec)

        // Assert
        Assertions.assertEquals(dto, createdDto)
        Mockito.verify(mockedCreateUseCase, Mockito.times(1)).execute(spec)
        Mockito.verify(mockedCreateUseCase, Mockito.times(1)).execute(TestHelper.MockitoHelper.anyObject())
    }
}