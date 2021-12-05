package com.graphite.competitionplanner.draw.repository

import com.graphite.competitionplanner.category.interfaces.ICategoryRepository
import com.graphite.competitionplanner.club.interfaces.IClubRepository
import com.graphite.competitionplanner.competition.interfaces.ICompetitionRepository
import com.graphite.competitionplanner.competitioncategory.interfaces.ICompetitionCategoryRepository
import com.graphite.competitionplanner.draw.interfaces.ICompetitionDrawRepository
import com.graphite.competitionplanner.player.repository.PlayerRepository
import com.graphite.competitionplanner.registration.interfaces.IRegistrationRepository
import com.graphite.competitionplanner.result.repository.ResultRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestGetWhenWinnerIsSet(
    @Autowired val resultRepository: ResultRepository,
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
    fun canGetSinglesPlayOff() {
        // TODO: Fix test
//        // Setup
//        val club = clubRepository.store(dataGenerator.newClubSpec())
//        val competition = club.addCompetition()
//        val competitionCategory = competition.createCategory()
//        val players = club.addPlayers(4)
//        val registrations = competitionCategory.registerPlayers(players)
//        val spec = PlayOffDrawSpec(
//            competitionCategoryId = competitionCategory.id,
//            startingRound = Round.SEMI_FINAL,
//            matches = listOf(
//                PlayOffMatch(
//                    registrationOneId = Registration.Real(registrations[0].id),
//                    registrationTwoId = Registration.Real(registrations[1].id),
//                    order = 1,
//                    round = Round.SEMI_FINAL
//                ),
//                PlayOffMatch(
//                    registrationOneId = Registration.Real(registrations[2].id),
//                    registrationTwoId = Registration.Real(registrations[3].id),
//                    order = 2,
//                    round = Round.SEMI_FINAL
//                ),
//                PlayOffMatch(
//                    registrationOneId = Registration.Placeholder,
//                    registrationTwoId = Registration.Placeholder,
//                    order = 1,
//                    round = Round.FINAL
//                )
//
//            )
//        )
//
//        val draw = repository.store(spec)
//        val matchId = draw.playOff.first().id
//        resultRepository.u
//
//        // Act
//        val result = repository.get(competitionCategory.id)
//

    }
}