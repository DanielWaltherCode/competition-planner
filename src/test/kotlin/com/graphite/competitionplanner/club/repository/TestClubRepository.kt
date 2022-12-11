package com.graphite.competitionplanner.club.repository

import com.graphite.competitionplanner.common.exception.NotFoundException
import com.graphite.competitionplanner.competition.repository.CompetitionRepository
import com.graphite.competitionplanner.util.DataGenerator
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.dao.DuplicateKeyException

@SpringBootTest
class TestClubRepository(
    @Autowired val clubRepository: ClubRepository,
    @Autowired val competitionRepository: CompetitionRepository
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
        val spec = dataGenerator.newClubSpec()
        val dto = clubRepository.store(spec)

        // Act
        val found = clubRepository.findByName(spec.name)

        // Assert
        Assertions.assertEquals(dto, found)
    }

    @Test
    fun shouldNotBeAbleToStoreClubsWithIdenticalNames() {
        // Setup
        val club = dataGenerator.newClubSpec()

        // Act
        clubRepository.store(club)

        // Assert
        Assertions.assertThrows(DuplicateKeyException::class.java) {
            clubRepository.store(club)
        }
    }

    @Test
    fun shouldNotBeAbleToStoreCompetitionSpecificClubsWithIdenticalName() {
        // Setup
        val organizingClub = clubRepository.store(dataGenerator.newClubSpec())
        val competition = competitionRepository.store(
            dataGenerator.newCompetitionSpec(organizingClubId = organizingClub.id))

        val club = dataGenerator.newClubSpec()

        // Act
        clubRepository.storeForCompetition(competition.id, club)

        // Assert
        Assertions.assertThrows(DuplicateKeyException::class.java) {
            clubRepository.storeForCompetition(competition.id, club)
        }
    }

    @Test
    fun shouldBeAbleToStoreCompetitionSpecificAndGlobalClubsWithIdenticalName() {
        // Setup
        val organizingClub = clubRepository.store(dataGenerator.newClubSpec())
        val competition = competitionRepository.store(
            dataGenerator.newCompetitionSpec(organizingClubId = organizingClub.id))

        val club = dataGenerator.newClubSpec(name = organizingClub.name)

        // Act & Assert
        Assertions.assertDoesNotThrow {
            clubRepository.storeForCompetition(competition.id, club)
        }
    }

    @Test
    fun shouldGetAllClubs() {
        // Setup
        val club1 = dataGenerator.newClubSpec()
        val club2 = dataGenerator.newClubSpec()
        val club3 = dataGenerator.newClubSpec()
        val club4 = dataGenerator.newClubSpec()
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
    }

}