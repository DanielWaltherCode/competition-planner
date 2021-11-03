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
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestDelete(
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
    fun canDeleteDraw() {
        // Setup
        val club = clubRepository.store(dataGenerator.newClubSpec())
        val competition = club.addCompetition()
        val competitionCategory = competition.createCategory()
        val players = club.addPlayers(4)
        val registrations = competitionCategory.registerPlayers(players)
        val registrationIds = registrations.map { Registration.Real(it.id) }
        val spec = GroupsDrawSpec(
            competitionCategoryId = competitionCategory.id,
            groups = listOf(
                Group(
                    name = "A",
                    registrationIds = registrationIds.take(3),
                    matches = listOf(
                        GroupMatch(registrationIds[0], registrationIds[1]),
                        GroupMatch(registrationIds[0], registrationIds[2]),
                        GroupMatch(registrationIds[0], registrationIds[3]),
                        GroupMatch(registrationIds[1], registrationIds[2]),
                        GroupMatch(registrationIds[1], registrationIds[3]),
                        GroupMatch(registrationIds[2], registrationIds[3])
                    )
                )
            ),
            matches = listOf(
                PlayOffMatch(
                    registrationOneId = Registration.Real(registrations[0].id),
                    registrationTwoId = Registration.Real(registrations[1].id),
                    order = 1,
                    round = Round.SEMI_FINAL
                ),
                PlayOffMatch(
                    registrationOneId = Registration.Real(registrations[2].id),
                    registrationTwoId = Registration.Real(registrations[3].id),
                    order = 2,
                    round = Round.SEMI_FINAL
                ),
                PlayOffMatch(
                    registrationOneId = Registration.Placeholder,
                    registrationTwoId = Registration.Placeholder,
                    order = 1,
                    round = Round.FINAL
                )

            )
        )

        repository.store(spec)

        // Act
        repository.delete(competitionCategory.id)

        // Assert
        val draw = repository.get(competitionCategory.id)
        Assertions.assertEquals(0, draw.groupDraw.size)
        Assertions.assertEquals(0, draw.playOff.size)
    }
}