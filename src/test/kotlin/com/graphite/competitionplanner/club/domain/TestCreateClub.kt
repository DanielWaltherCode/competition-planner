package com.graphite.competitionplanner.club.domain

import com.graphite.competitionplanner.common.exception.BadRequestException
import com.graphite.competitionplanner.common.exception.BadRequestType
import com.graphite.competitionplanner.competition.domain.CreateCompetition
import com.graphite.competitionplanner.util.DataGenerator
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestCreateClub(
    @Autowired val createClub: CreateClub,
    @Autowired val createCompetition: CreateCompetition
) {

    val dataGenerator = DataGenerator()

    @Test
    fun shouldCreateClubIfNameIsAvailable() {
        // Setup
        val spec = dataGenerator.newClubSpec()

        // Act
        val club = createClub.execute(spec)

        // Assert
        Assertions.assertTrue(club.id > 0)
        Assertions.assertEquals(spec.name, club.name)
        Assertions.assertEquals(spec.address, club.address)
    }

    @Test
    fun shouldGetBadRequestExceptionWhenClubWithSameNameAlreadyExist() {
        // Setup
        val spec = dataGenerator.newClubSpec()
        val club = createClub.execute(spec)

        // Act & Assert
        val e = Assertions.assertThrows(BadRequestException::class.java) {
            createClub.execute(spec)
        }

        Assertions.assertEquals(BadRequestType.CLUB_NAME_NOT_UNIQUE, e.exceptionType)
        Assertions.assertEquals(
            "There is already a club registered with this name: ${club.name}",
            e.errorMessage)
    }

    @Test
    fun shouldGetBadRequestExceptionWhenRegisteringSameClubTwiceToTheSameCompetition() {
        // Setup
        val organizingClub = createClub.execute(dataGenerator.newClubSpec())
        val competition = createCompetition.execute(
            dataGenerator.newCompetitionSpec(organizingClubId = organizingClub.id))

        val spec = dataGenerator.newClubSpec()
        val club = createClub.executeForCompetition(competition.id, spec)

        // Act & Assert
        val e = Assertions.assertThrows(BadRequestException::class.java) {
            createClub.executeForCompetition(competition.id, spec)
        }

        Assertions.assertEquals(BadRequestType.CLUB_NAME_NOT_UNIQUE, e.exceptionType)
        Assertions.assertEquals(
            "There is already a club registered to this competition with this name: ${club.name}",
            e.errorMessage)
    }

    @Test
    fun shouldBeAbleToAddClubThenCompetitionSpecificClubWithSameName() {
        // Setup
        val organizingClub = createClub.execute(dataGenerator.newClubSpec())
        val competition = createCompetition.execute(
            dataGenerator.newCompetitionSpec(organizingClubId = organizingClub.id))

        val spec = dataGenerator.newClubSpec()

        // Act & Assert
        createClub.executeForCompetition(competition.id, spec)
        Assertions.assertDoesNotThrow {
            createClub.execute(spec)
        }
    }

    @Test
    fun shouldBeAbleToAddCompetitionSpecificClubThenClubWithSameName() {
        // Setup
        val organizingClub = createClub.execute(dataGenerator.newClubSpec())
        val competition = createCompetition.execute(
            dataGenerator.newCompetitionSpec(organizingClubId = organizingClub.id))

        val spec = dataGenerator.newClubSpec()

        // Act & Assert
        createClub.execute(spec)
        Assertions.assertDoesNotThrow {
            createClub.executeForCompetition(competition.id, spec)
        }
    }

}