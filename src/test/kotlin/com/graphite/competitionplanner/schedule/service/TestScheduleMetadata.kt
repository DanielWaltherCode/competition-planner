package com.graphite.competitionplanner.schedule.service

import com.graphite.competitionplanner.category.interfaces.ICategoryRepository
import com.graphite.competitionplanner.club.interfaces.IClubRepository
import com.graphite.competitionplanner.competition.domain.CreateCompetition
import com.graphite.competitionplanner.competition.interfaces.CompetitionSpec
import com.graphite.competitionplanner.competition.interfaces.ICompetitionRepository
import com.graphite.competitionplanner.competition.interfaces.LocationSpec
import com.graphite.competitionplanner.competitioncategory.interfaces.ICompetitionCategoryRepository
import com.graphite.competitionplanner.match.repository.MatchRepository
import com.graphite.competitionplanner.player.interfaces.IPlayerRepository
import com.graphite.competitionplanner.registration.interfaces.IRegistrationRepository
import com.graphite.competitionplanner.result.interfaces.IResultRepository
import com.graphite.competitionplanner.schedule.api.ScheduleMetadataSpec
import com.graphite.competitionplanner.util.BaseRepositoryTest
import com.graphite.competitionplanner.util.Util
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDate

@SpringBootTest
class TestScheduleMetadata(
    @Autowired val util: Util,
    @Autowired val scheduleMetadataService: ScheduleMetadataService,
    @Autowired val createCompetition: CreateCompetition,
    @Autowired clubRepository: IClubRepository,
    @Autowired competitionRepository: ICompetitionRepository,
    @Autowired competitionCategoryRepository: ICompetitionCategoryRepository,
    @Autowired categoryRepository: ICategoryRepository,
    @Autowired playerRepository: IPlayerRepository,
    @Autowired registrationRepository: IRegistrationRepository,
    @Autowired matchRepository: MatchRepository,
    @Autowired resultRepository: IResultRepository,
) : BaseRepositoryTest(
    clubRepository,
    competitionRepository,
    competitionCategoryRepository,
    categoryRepository,
    playerRepository,
    registrationRepository,
    matchRepository,
    resultRepository
) {
    var competitionId = 0

    @BeforeEach
    fun addCompetition() {
        val club = newClub()
        competitionId = createCompetition.execute(
            CompetitionSpec(
                location = LocationSpec("Lund"),
                name = "Eurofinans 2023",
                welcomeText = "Välkomna till Eurofinans",
                organizingClubId = club.id,
                competitionLevel = "A",
                startDate = LocalDate.now(),
                endDate = LocalDate.now().plusDays(10)
            )
        ).id
    }

    @Test
    fun getScheduleMetadata() {
        val scheduleMetadataDTO = scheduleMetadataService.getScheduleMetadata(competitionId)
        Assertions.assertNotNull(scheduleMetadataDTO)
        Assertions.assertNotNull(scheduleMetadataDTO.id)
        Assertions.assertNotNull(scheduleMetadataDTO.minutesPerMatch)
        Assertions.assertNotNull(scheduleMetadataDTO.pauseBetweenGroupMatches)
        Assertions.assertNotNull(scheduleMetadataDTO.pauseBetweenPlayoffMatches)
        Assertions.assertNotNull(scheduleMetadataDTO.pauseAfterGroupStage)
    }

    @Test
    fun updateScheduleMetadata() {
        val originalMetadataDTO = scheduleMetadataService.getScheduleMetadata(competitionId)
        val newPauseInGroup = 20 // Minutes

        val specToAdd = ScheduleMetadataSpec(
            originalMetadataDTO.minutesPerMatch,
            originalMetadataDTO.pauseAfterGroupStage,
            newPauseInGroup,
            originalMetadataDTO.pauseBetweenPlayoffMatches
        )

        val updatedDTO = scheduleMetadataService.updateScheduleMetadata(originalMetadataDTO.id, competitionId, specToAdd)
        Assertions.assertEquals(updatedDTO.minutesPerMatch, originalMetadataDTO.minutesPerMatch)
        Assertions.assertEquals(updatedDTO.pauseAfterGroupStage, originalMetadataDTO.pauseAfterGroupStage)
        Assertions.assertEquals(updatedDTO.pauseBetweenGroupMatches, newPauseInGroup)
        Assertions.assertEquals(updatedDTO.pauseBetweenPlayoffMatches, originalMetadataDTO.pauseBetweenPlayoffMatches)
    }
}