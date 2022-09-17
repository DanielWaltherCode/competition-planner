package com.graphite.competitionplanner.common.repository

import com.graphite.competitionplanner.club.interfaces.ClubDTO
import com.graphite.competitionplanner.club.interfaces.IClubRepository
import com.graphite.competitionplanner.util.DataGenerator
import org.jooq.DSLContext
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.stereotype.Repository
import kotlin.RuntimeException

@SpringBootTest
class TestBaseRepository(
    @Autowired val repository: MyTestRepository,
    @Autowired val clubRepository: IClubRepository
) {

    private val dataGenerator = DataGenerator()

    @Test
    fun testAsTransaction() {
        // Setup
        val club = dataGenerator.newClubSpec()

        // Act
        try {
            repository.asTransaction {
                clubRepository.store(club)
                throw RuntimeException("Simulating a transaction failure")
            }
        }catch(_: Exception) {
            // Ignoring just so test is not marked as failed here
        }

        // Assert
        val allClubs = clubRepository.getAll()
        Assertions.assertTrue(
            allClubs.none { it.name == club.name },
            "Did not expect to find the club as the transaction failed"
        )
    }

    @Test
    fun shouldRethrowRuntimeException() {
        // Setup
        var caughtException = false

        // Act
        try {
            repository.asTransaction {
                throw RuntimeException("Simulating a transaction failure")
            }
            Assertions.fail("We should not get here as the base class should rethrow exception")
        }catch(_: RuntimeException) {
            caughtException = true
        }

        // Assert
        Assertions.assertTrue(caughtException, "We expected to catch a RuntimeException")
    }

    @Test
    fun testAsTransactionWithLateInit() {
        // Setup
        val club = dataGenerator.newClubSpec()

        // Act
        lateinit var dto: ClubDTO
        repository.asTransaction {
            dto = clubRepository.store(club)
        }

        // Assert
        Assertions.assertNotNull(dto, "The value should have been set")
    }
}

/**
 * A repository used for testing the abstract class BaseRepository
 */
@Repository
class MyTestRepository(dslContext: DSLContext) : BaseRepository(dslContext)