package com.graphite.competitionplanner.api

import com.graphite.competitionplanner.repositories.ClubRepository
import com.graphite.competitionplanner.repositories.MatchRepository
import com.graphite.competitionplanner.repositories.PlayerRepository
import com.graphite.competitionplanner.repositories.RegistrationRepository
import com.graphite.competitionplanner.repositories.competition.CompetitionCategoryRepository
import com.graphite.competitionplanner.repositories.competition.CompetitionRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class TestHelper(
    @Autowired val clubRepository: ClubRepository,
    @Autowired val playerRepository: PlayerRepository,
    @Autowired val matchRepository: MatchRepository,
    @Autowired val registrationRepository: RegistrationRepository,
    @Autowired val competitionRepository: CompetitionRepository,
    @Autowired val competitionCategoryRepository: CompetitionCategoryRepository,
    @Autowired val categoryRepository: CompetitionCategoryRepository
) {

    fun cleanUpAll(){
        matchRepository.clearTable()
        registrationRepository.clearPlayingIn()
        registrationRepository.clearPlayerRegistration()
        registrationRepository.clearRegistration()
        competitionCategoryRepository.clearTable()
        competitionRepository.clearTable()
        playerRepository.clearRankingTable()
        playerRepository.clearTable()
        categoryRepository.clearTable()
        clubRepository.clearClubTable()
    }

    fun anyPlayerSpecFor(club: ClubDTO) : PlayerSpec {
        return PlayerSpec(
            "Laban",
            "Nilsson",
            ClubNoAddressDTO(
                club.id!!,
                club.name
            ),
            LocalDate.now().minusMonths(170)
        )

    }
}