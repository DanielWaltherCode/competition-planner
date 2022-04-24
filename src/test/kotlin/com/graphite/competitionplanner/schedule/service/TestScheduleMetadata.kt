package com.graphite.competitionplanner.schedule.service

import com.graphite.competitionplanner.competition.domain.CreateCompetition
import com.graphite.competitionplanner.competition.interfaces.CompetitionSpec
import com.graphite.competitionplanner.competition.interfaces.LocationSpec
import com.graphite.competitionplanner.competition.repository.CompetitionRepository
import com.graphite.competitionplanner.schedule.api.ScheduleMetadataSpec
import com.graphite.competitionplanner.util.Util
import org.junit.jupiter.api.AfterEach
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
    @Autowired val competitionRepository: CompetitionRepository,
    @Autowired val createCompetition: CreateCompetition
) {

    var competitionId = 0

    @BeforeEach
    fun addCompetition() {
        competitionId = createCompetition.execute(
            CompetitionSpec(
                location = LocationSpec("Lund"),
                name = "Eurofinans 2021",
                welcomeText = "Välkomna till Eurofinans",
                organizingClubId = util.getClubIdOrDefault("Lugi"),
                competitionLevel = "A",
                startDate = LocalDate.now(),
                endDate = LocalDate.now().plusDays(10)
            )
        ).id
    }

    @AfterEach
    fun deleteCompetition() {
        competitionRepository.deleteCompetition(competitionId)
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

        val specToAddd = ScheduleMetadataSpec(
            originalMetadataDTO.minutesPerMatch,
            originalMetadataDTO.pauseAfterGroupStage,
            newPauseInGroup,
            originalMetadataDTO.pauseBetweenPlayoffMatches
        )

        val updatedDTO = scheduleMetadataService.updateScheduleMetadata(originalMetadataDTO.id, competitionId, specToAddd)
        Assertions.assertEquals(updatedDTO.minutesPerMatch, originalMetadataDTO.minutesPerMatch)
        Assertions.assertEquals(updatedDTO.pauseAfterGroupStage, originalMetadataDTO.pauseAfterGroupStage)
        Assertions.assertEquals(updatedDTO.pauseBetweenGroupMatches, newPauseInGroup)
        Assertions.assertEquals(updatedDTO.pauseBetweenPlayoffMatches, originalMetadataDTO.pauseBetweenPlayoffMatches)
    }
}