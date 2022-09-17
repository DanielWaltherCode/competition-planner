package com.graphite.competitionplanner.club.domain

import com.graphite.competitionplanner.club.interfaces.ClubDTO
import com.graphite.competitionplanner.club.interfaces.IClubRepository
import com.graphite.competitionplanner.club.repository.ClubPaymentRepository
import com.graphite.competitionplanner.util.DataGenerator
import com.graphite.competitionplanner.util.TestHelper
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestCreateClub {

    private final val mockedClubRepository = mock(IClubRepository::class.java)
    private final val mockedPaymentRepository = mock(ClubPaymentRepository::class.java)
    private final val createClub = CreateClub(mockedClubRepository, mockedPaymentRepository)
    val dataGenerator = DataGenerator()

    @Test
    fun shouldCreateClubIfNameIsAvailable() {
        // Setup
        val spec = dataGenerator.newClubSpec("ClubX", "Address1")
        val otherClubs = listOf(
            dataGenerator.newClubDTO(name = "ClubB"),
            dataGenerator.newClubDTO(name = "ClubC"),
            dataGenerator.newClubDTO(name = "cluba")
        )
        `when`(mockedClubRepository.getAll()).thenReturn(otherClubs)
        `when`(mockedClubRepository.store(spec)).thenReturn(ClubDTO(99, "TestClub", "asdf"))

        // Act
        createClub.execute(spec)

        // Assert
        verify(mockedClubRepository, times(1)).store(spec)
        verify(mockedClubRepository, times(1)).store(TestHelper.MockitoHelper.anyObject())
    }

    @Test
    fun shouldNotSaveWhenClubWithSameNameAlreadyExist() {
        // Setup
        val spec = dataGenerator.newClubSpec("ClubA", "Address1")
        val clubWithSameName = ClubDTO(123, spec.name, "Address2")
        `when`(mockedClubRepository.getAll()).thenReturn(listOf(clubWithSameName))

        // Act & Assert
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            createClub.execute(spec)
        }
        verify(mockedClubRepository, never()).store(spec)
    }

}