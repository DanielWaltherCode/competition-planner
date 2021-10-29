package com.graphite.competitionplanner.draw.repository

import com.graphite.competitionplanner.category.interfaces.ICategoryRepository
import com.graphite.competitionplanner.club.interfaces.IClubRepository
import com.graphite.competitionplanner.competition.interfaces.ICompetitionRepository
import com.graphite.competitionplanner.competitioncategory.interfaces.ICompetitionCategoryRepository
import com.graphite.competitionplanner.domain.entity.Round
import com.graphite.competitionplanner.draw.domain.*
import com.graphite.competitionplanner.draw.interfaces.ICompetitionDrawRepository
import com.graphite.competitionplanner.player.repository.PlayerRepository
import com.graphite.competitionplanner.registration.interfaces.IRegistrationRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class TestStore(
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

    @Test
    fun canStorePlayOff() {
        // Setup
        val club = clubRepository.store(dataGenerator.newClubSpec())
        val competition = club.addCompetition()
        val competitionCategory = competition.createCategory()
        val players = club.addPlayers(2)
        val registrations = competitionCategory.registerPlayers(players)
        val spec = CompetitionCategoryPlayOffDrawSpec(
            competitionCategoryId = competitionCategory.id,
            startingRound = Round.FINAL,
            matches = listOf(
                PlayOffMatch(
                    registrationOneId = Registration.Real(registrations.first().id),
                    registrationTwoId = Registration.Real(registrations.last().id),
                    order = 1,
                    round = Round.FINAL
                )
            )
        )

        // Act
        repository.store(spec)
    }

    @Test
    fun canStorePlayOffWithBye() {
        // Setup
        val club = clubRepository.store(dataGenerator.newClubSpec())
        val competition = club.addCompetition()
        val competitionCategory = competition.createCategory()
        val players = club.addPlayers(1)
        val registrations = competitionCategory.registerPlayers(players)
        val spec = CompetitionCategoryPlayOffDrawSpec(
            competitionCategoryId = competitionCategory.id,
            startingRound = Round.FINAL,
            matches = listOf(
                PlayOffMatch(
                    registrationOneId = Registration.Real(registrations.first().id),
                    registrationTwoId = Registration.Bye,
                    order = 1,
                    round = Round.FINAL
                )
            )
        )

        // Act
        repository.store(spec)
    }

    @Test
    fun canStorePlayOffWithPlaceholder() {
        // Setup
        val club = clubRepository.store(dataGenerator.newClubSpec())
        val competition = club.addCompetition()
        val competitionCategory = competition.createCategory()
        val players = club.addPlayers(1)
        val registrations = competitionCategory.registerPlayers(players)
        val spec = CompetitionCategoryPlayOffDrawSpec(
            competitionCategoryId = competitionCategory.id,
            startingRound = Round.FINAL,
            matches = listOf(
                PlayOffMatch(
                    registrationOneId = Registration.Placeholder,
                    registrationTwoId = Registration.Real(registrations.first().id),
                    order = 1,
                    round = Round.FINAL
                )
            )
        )

        // Act
        repository.store(spec)
    }

    @Test
    fun canStoreGroupMatches() {
        val club = clubRepository.store(dataGenerator.newClubSpec())
        val competition = club.addCompetition()
        val competitionCategory = competition.createCategory()
        val players = club.addPlayers(4)
        val registrations = competitionCategory.registerPlayers(players)
        val registrationIds = registrations.map { Registration.Real(it.id) }
        val spec = CompetitionCategoryGroupsDrawSpec(
            competitionCategoryId = competitionCategory.id,
            groups = listOf(
                Group(
                    name = "A",
                    registrationIds = registrationIds.take(2),
                    matches = listOf(GroupMatch(registrationIds[0], registrationIds[1]))
                ),
                Group(
                    name = "B",
                    registrationIds = registrationIds.takeLast(2),
                    matches = listOf(GroupMatch(registrationIds[2], registrationIds[3]))
                )
            ),
            matches = listOf(
                PlayOffMatch(
                    registrationOneId = Registration.Placeholder,
                    registrationTwoId = Registration.Placeholder,
                    order = 1,
                    round = Round.FINAL
                )
            )
        )

        repository.store(spec)
    }
}