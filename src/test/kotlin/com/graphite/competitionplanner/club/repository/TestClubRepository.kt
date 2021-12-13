package com.graphite.competitionplanner.club.repository

import com.graphite.competitionplanner.common.exception.NotFoundException
import com.graphite.competitionplanner.util.DataGenerator
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestClubRepository(
    @Autowired val clubRepository: ClubRepository
) {

    val dataGenerator = DataGenerator()

    @Test
    fun shouldSetIdWhenSaving() {
        // Setup
        val spec = dataGenerator.newClubSpec("Luleå IK", "Stormarvägen 123")

        // Act
        val savedDto = clubRepository.store(spec)

        // Assert
        Assertions.assertTrue(savedDto.id != 0)

        // Clean up
        clubRepository.delete(savedDto.id)
    }

    @Test
    fun shouldThrowNotFoundExceptionWhenDeletingClubThatDoesNotExist() {
        Assertions.assertThrows(NotFoundException::class.java) { clubRepository.delete(-1) }
    }

    @Test
    fun shouldUpdate() {
        // Setup
        val spec = dataGenerator.newClubSpec("Ilskan IF", "Smilevägen 3A")
        val original = clubRepository.store(spec)
        val updateSpec = dataGenerator.newClubSpec("Nile IF", "Norrgränd")

        // Act
        val updated = clubRepository.update(original.id, updateSpec)

        // Assertions
        Assertions.assertEquals(updateSpec.name, updated.name)
        Assertions.assertEquals(updateSpec.address, updated.address)

        // Clean up
        clubRepository.delete(updated.id)
    }

    @Test
    fun shouldThrowNotFoundExceptionWhenUpdatingClubThatDoesNotExist() {
        Assertions.assertThrows(NotFoundException::class.java) {
            clubRepository.update(-1, dataGenerator.newClubSpec())
        }
    }

    @Test
    fun shouldThrowNotFoundExceptionWhenNotFindingClubWithGivenName() {
        Assertions.assertThrows(NotFoundException::class.java) { clubRepository.findByName("NameThatDoesNotExist") }
    }

    @Test
    fun shouldThrowNotFoundExceptionWhenNotFindingClubWithGivenId() {
        Assertions.assertThrows(NotFoundException::class.java) { clubRepository.findById(-19) }
    }

    @Test
    fun shouldReturnDtoWhenSearchingForClubWithGiven() {
        // Setup
        val spec = dataGenerator.newClubSpec("Svalnäs IK", "Address 123")
        val dto = clubRepository.store(spec)

        // Act
        val found = clubRepository.findByName(spec.name)

        // Assert
        Assertions.assertEquals(dto, found)
    }

    @Test
    fun shouldGetAllClubs() {
        // Setup
        val club1 = dataGenerator.newClubSpec("ClubA", "AddressA")
        val club2 = dataGenerator.newClubSpec("ClubB", "AddressB")
        val club3 = dataGenerator.newClubSpec("ClubC", "AddressC")
        val club4 = dataGenerator.newClubSpec("ClubD", "AddressD")
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
            clubRepository.delete(club.id)
        }
    }
}