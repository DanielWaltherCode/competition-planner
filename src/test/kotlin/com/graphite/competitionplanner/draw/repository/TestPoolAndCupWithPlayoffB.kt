package com.graphite.competitionplanner.draw.repository

import com.graphite.competitionplanner.category.interfaces.ICategoryRepository
import com.graphite.competitionplanner.club.interfaces.IClubRepository
import com.graphite.competitionplanner.competition.interfaces.ICompetitionRepository
import com.graphite.competitionplanner.competitioncategory.interfaces.ICompetitionCategoryRepository
import com.graphite.competitionplanner.draw.domain.PlayOffMatch
import com.graphite.competitionplanner.draw.domain.Pool
import com.graphite.competitionplanner.draw.domain.PoolAndCupDrawWithBPlayoffSpec
import com.graphite.competitionplanner.draw.domain.PoolMatch
import com.graphite.competitionplanner.draw.interfaces.ICompetitionDrawRepository
import com.graphite.competitionplanner.draw.interfaces.Round
import com.graphite.competitionplanner.player.repository.PlayerRepository
import com.graphite.competitionplanner.registration.domain.Registration
import com.graphite.competitionplanner.registration.interfaces.IRegistrationRepository
import com.graphite.competitionplanner.util.SetupTestData
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class TestPoolAndCupWithPlayoffB(
    @Autowired val setupTestData: SetupTestData,
    @Autowired repository: ICompetitionDrawRepository,
    @Autowired clubRepository: IClubRepository,
    @Autowired competitionRepository: ICompetitionRepository,
    @Autowired competitionCategoryRepository: ICompetitionCategoryRepository,
    @Autowired categoryRepository: ICategoryRepository,
    @Autowired playerRepository: PlayerRepository,
    @Autowired registrationRepository: IRegistrationRepository
) : TestCompetitionDrawRepository(repository,
    clubRepository,
    competitionRepository,
    competitionCategoryRepository,
    categoryRepository,
    playerRepository,
    registrationRepository) {

    @BeforeEach
    fun setupByeAndPlaceholderRegistrations() {
        setupTestData.trySetupOverigtClub()
        setupTestData.trySetupByeAndPlaceHolder()
    }

    @Test
    fun canStoreBPlayoff(){
        // Setup
        val club = clubRepository.store(dataGenerator.newClubSpec())
        val competition = club.addCompetition()
        val competitionCategory = competition.createCategory()
        val players = club.addPlayers(6)
        val registrations = competitionCategory.registerPlayers(players)
        val registrationIds = registrations.map { Registration.Real(it.id) }

        val poolA = Pool(
            name = "A",
            registrationIds = registrationIds,
            matches = listOf(
                PoolMatch(registrationIds[0], registrationIds[1]),
                PoolMatch(registrationIds[0], registrationIds[2]),
                PoolMatch(registrationIds[1], registrationIds[2])
            )
        )
        val poolB = Pool(
            name = "B",
            registrationIds = registrationIds,
            matches = listOf(
                PoolMatch(registrationIds[3], registrationIds[4]),
                PoolMatch(registrationIds[3], registrationIds[5]),
                PoolMatch(registrationIds[4], registrationIds[5])
            )
        )

        val spec = PoolAndCupDrawWithBPlayoffSpec(
            competitionCategoryId = competitionCategory.id,
            pools = listOf(poolA, poolB),
            aPlayoff = listOf(
                PlayOffMatch(
                    registrationOneId = Registration.Placeholder("A1"),
                    registrationTwoId = Registration.Placeholder("B1"),
                    order = 1,
                    round = Round.FINAL
                )
            ),
            bPlayoff = listOf(
                PlayOffMatch(
                    registrationOneId = Registration.Placeholder("A2"),
                    registrationTwoId = Registration.Placeholder("B3"),
                    order = 1,
                    round = Round.SEMI_FINAL
                ),
                PlayOffMatch(
                    registrationOneId = Registration.Placeholder("A3"),
                    registrationTwoId = Registration.Placeholder("B2"),
                    order = 2,
                    round = Round.SEMI_FINAL
                ),
                PlayOffMatch(
                    registrationOneId = Registration.Placeholder(),
                    registrationTwoId = Registration.Placeholder(),
                    order = 1,
                    round = Round.FINAL
                )
            )
        )

        val result = repository.store(spec)

        Assertions.assertEquals(2, result.groups.size)

        val groupA = result.groups.first { it.name == "A" }
        val groupB = result.groups.first { it.name == "B" }
        Assertions.assertEquals(3, groupA.matches.size, "Not the expected number of matches in group A")
        Assertions.assertEquals(3, groupB.matches.size, "Not the expected number of matches in group B")
        Assertions.assertEquals(3, groupA.players.size, "Not the expected number of players in group A")
        Assertions.assertEquals(3, groupB.players.size, "Not the expected number of players in group B")

        Assertions.assertEquals(1, result.playOff.size, "Not the expected number of rounds in playoff A")
        Assertions.assertEquals(2, result.playOffB.size, "Not the expected number of rounds in playoff B")

        val finalPlayoffA = result.playOff.first()
        Assertions.assertEquals(Round.FINAL, finalPlayoffA.round)
        Assertions.assertEquals(1, finalPlayoffA.matches.size, "Not the expected number of matches in playoff A")
        Assertions.assertEquals("A1", finalPlayoffA.matches.first().firstPlayer.first().firstName)
        Assertions.assertEquals("B1", finalPlayoffA.matches.first().secondPlayer.first().firstName)

        val semifinal = result.playOffB.first()
        Assertions.assertEquals(Round.SEMI_FINAL, semifinal.round)
        Assertions.assertEquals(2, semifinal.matches.size, "Not the expected number of matches in playoff B semi finals")
        Assertions.assertEquals("A2", semifinal.matches.first().firstPlayer.first().firstName)
        Assertions.assertEquals("B3", semifinal.matches.first().secondPlayer.first().firstName)
        Assertions.assertEquals("A3", semifinal.matches.last().firstPlayer.first().firstName)
        Assertions.assertEquals("B2", semifinal.matches.last().secondPlayer.first().firstName)

        val finalPlayoffB = result.playOffB.last()
        Assertions.assertEquals(Round.FINAL, finalPlayoffB.round)
        Assertions.assertEquals(1, finalPlayoffB.matches.size, "Not the expected number of matches in playoff B finals")
        Assertions.assertEquals("Placeholder", finalPlayoffB.matches.first().firstPlayer.first().firstName)
        Assertions.assertEquals("Placeholder", finalPlayoffB.matches.first().secondPlayer.first().firstName)
    }
}