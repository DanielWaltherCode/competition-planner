package com.graphite.competitionplanner.domain.entity

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import java.lang.IllegalArgumentException
import java.time.LocalDate

@SpringBootTest
class TestPlayer {

    val club = Club(0, "Lule IK", "Sjögatan")

    @Test
    fun firstNameCannotBeEmpty() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            Player(0, "", "lastname", club, LocalDate.of(1992, 10, 18))
        }
    }

    @Test
    fun firstNameCannotContainNumbers() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            Player(0, "Lennart1", "lastname", club, LocalDate.of(1992, 10, 18))
        }
    }

    @Test
    fun firstNameCanContainNonEnglishLetters() {
        Assertions.assertDoesNotThrow {
            Player(0, "ÅÄÖГ敗", "lastname", club, LocalDate.of(1992, 10, 18))
        }
    }

    @Test
    fun lastNameCannotBeEmpty() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            Player(0, "firstname", "", club, LocalDate.of(1992, 10, 18))
        }
    }

    @Test
    fun lastNameCannotContainLetters() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            Player(0, "firstname", "lastname123", club, LocalDate.of(1992, 10, 18))
        }
    }

    @Test
    fun lastNameCanContainNonEnglishLetters() {
        Assertions.assertDoesNotThrow {
            Player(0, "firstname", "ÅÄÖГ敗", club, LocalDate.of(1992, 10, 18))
        }
    }

    @Test
    fun dateOfBirthCannotBeInTheFuture() {
        val future = LocalDate.now().plusDays(100)
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            Player(0, "firstname", "lastName", club, future)
        }
    }
}