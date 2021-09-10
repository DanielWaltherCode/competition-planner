package com.graphite.competitionplanner.club.repository

import com.graphite.competitionplanner.club.domain.interfaces.ClubDTO
import com.graphite.competitionplanner.common.exception.NotFoundException
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestClubRepository(
    @Autowired val clubRepository: ClubRepository
) {

    @Test
    fun shouldSetIdWhenSaving() {
        // Setup
        val dto = ClubDTO(0, "Luleå IK", "Stormarvägen 123")

        // Act
        val savedDto = clubRepository.store(dto)

        // Assert
        Assertions.assertTrue(savedDto.id != 0)

        // Clean up
        clubRepository.delete(savedDto)
    }

    @Test
    fun shouldThrowNotFoundExceptionWhenDeletingClubThatDoesNotExist() {
        // Setup
        val dto = ClubDTO(10, "", "")

        // Act
        Assertions.assertThrows(NotFoundException::class.java) { clubRepository.delete(dto) }
    }

    @Test
    fun shouldUpdate() {
        // Setup
        val original = ClubDTO(0, "Ilskan IF", "Smilevägen 3A")
        val savedDto = clubRepository.store(original)
        val dto = ClubDTO(savedDto.id, "Nile IF", "Norrgränd")

        // Act
        val updated = clubRepository.update(dto)

        // Clean up
        clubRepository.delete(updated)
    }

    @Test
    fun shouldThrowNotFoundExceptionWhenUpdatingClubThatDoesNotExist() {
        // Setup
        val dto = ClubDTO(123, "Illeå IF", "Smaragdvägen 1A")

        // Act
        Assertions.assertThrows(NotFoundException::class.java) { clubRepository.update(dto) }
    }

    @Test
    fun shouldThrowNotFoundExceptionWhenNotFindingClubWithGivenName() {
        // Act & Assert
        Assertions.assertThrows(NotFoundException::class.java) { clubRepository.findByName("NameThatDoesNotExist") }
    }

    @Test
    fun shouldThrowNotFoundExceptionWhenNotFindingClubWithGivenId() {
        // Act & Assert
        Assertions.assertThrows(NotFoundException::class.java) { clubRepository.findById(-19) }
    }

    @Test
    fun shouldReturnDtoWhenSearchingForClubWithGiven() {
        // Setup
        val dto = ClubDTO(0, "Svalnäs IK", "Address 123")
        val saved = clubRepository.store(dto)

        // Act
        val found = clubRepository.findByName(dto.name)

        // Assert
        Assertions.assertEquals(saved, found)
    }

    @Test
    fun shouldGetAllClubs() {
        // Setup
        val club1 = ClubDTO(0, "ClubA", "AddressA")
        val club2 = ClubDTO(0, "ClubB", "AddressB")
        val club3 = ClubDTO(0, "ClubC", "AddressC")
        val club4 = ClubDTO(0, "ClubD", "AddressD")
        val clubs = listOf(club1, club2, club3, club4)
        for (club in clubs) {
            clubRepository.store(club)
        }

        // Act
        val foundClubs = clubRepository.getAll()

        // Assert
        val foundClubNames = foundClubs.map { it.name }
        for (club in clubs) {
            Assertions.assertTrue(foundClubNames.contains(club.name))
        }

        // Clean up
        val createdClubs = foundClubs.filter { clubs.map { c -> c.name }.contains(it.name) }
        for (club in createdClubs) {
            clubRepository.delete(club)
        }
    }
}