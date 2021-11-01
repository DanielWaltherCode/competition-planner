package com.graphite.competitionplanner.player.domain

import com.graphite.competitionplanner.util.DataGenerator
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDate

@SpringBootTest
class TestPlayerSpec {

    private final val dataGenerator = DataGenerator()
    val club = dataGenerator.newClubDTO()

    @Test
    fun firstNameCannotBeEmpty() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            dataGenerator.newPlayerSpec(firstName = "")
        }
    }

    @Test
    fun firstNameCannotContainNumbers() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            dataGenerator.newPlayerSpec(firstName = "Lennart1")
        }
    }

    @Test
    fun firstNameCanContainNonEnglishLetters() {
        Assertions.assertDoesNotThrow {
            dataGenerator.newPlayerSpec(firstName = "ÅÄÖГ敗")
        }
    }

    @Test
    fun lastNameCannotBeEmpty() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            dataGenerator.newPlayerSpec(lastName = "")
        }
    }

    @Test
    fun lastNameCannotContainLetters() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            dataGenerator.newPlayerSpec(lastName = "lastname123")
        }
    }

    @Test
    fun lastNameCanContainNonEnglishLetters() {
        Assertions.assertDoesNotThrow {
            dataGenerator.newPlayerSpec(lastName = "ÅÄÖГ敗")
        }
    }

    @Test
    fun dateOfBirthCannotBeInTheFuture() {
        val future = LocalDate.now().plusDays(100)
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            dataGenerator.newPlayerSpec(dateOfBirth = future)
        }
    }
}