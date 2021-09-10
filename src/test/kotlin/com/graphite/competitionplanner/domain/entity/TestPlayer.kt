package com.graphite.competitionplanner.domain.entity

import com.graphite.competitionplanner.util.DataGenerator
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDate

@SpringBootTest
class TestPlayer() {

    private final val dataGenerator = DataGenerator()
    val club = dataGenerator.newClub()

    @Test
    fun firstNameCannotBeEmpty() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            dataGenerator.newPlayer(firstName = "")
        }
    }

    @Test
    fun firstNameCannotContainNumbers() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            dataGenerator.newPlayer(firstName = "Lennart1")
        }
    }

    @Test
    fun firstNameCanContainNonEnglishLetters() {
        Assertions.assertDoesNotThrow {
            dataGenerator.newPlayer(firstName = "ÅÄÖГ敗")
        }
    }

    @Test
    fun lastNameCannotBeEmpty() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            dataGenerator.newPlayer(lastName = "")
        }
    }

    @Test
    fun lastNameCannotContainLetters() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            dataGenerator.newPlayer(lastName = "lastname123")
        }
    }

    @Test
    fun lastNameCanContainNonEnglishLetters() {
        Assertions.assertDoesNotThrow {
            dataGenerator.newPlayer(lastName = "ÅÄÖГ敗")
        }
    }

    @Test
    fun dateOfBirthCannotBeInTheFuture() {
        val future = LocalDate.now().plusDays(100)
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            dataGenerator.newPlayer(dateOfBirth = future)
        }
    }
}