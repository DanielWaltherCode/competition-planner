package com.graphite.competitionplanner.schedule

import com.graphite.competitionplanner.api.ScheduleMetadataSpec
import com.graphite.competitionplanner.api.competition.CompetitionSpec
import com.graphite.competitionplanner.repositories.ScheduleRepository
import com.graphite.competitionplanner.repositories.competition.CompetitionRepository
import com.graphite.competitionplanner.service.ScheduleService
import com.graphite.competitionplanner.service.competition.CompetitionService
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
    @Autowired val scheduleService: ScheduleService,
    @Autowired val scheduleRepository: ScheduleRepository,
    @Autowired val competitionService: CompetitionService,
    @Autowired val competitionRepository: CompetitionRepository

) {

    var competitionId = 0

    @BeforeEach
    fun addCompetition() {
        competitionId = competitionService.addCompetition(
            CompetitionSpec(
                location = "Lund",
                name = "Eurofinans 2021",
                welcomeText = "Välkomna till Eurofinans",
                organizingClubId = util.getClubIdOrDefault("Lugi"),
                startDate = LocalDate.now(),
                endDate = LocalDate.now().plusDays(10)
            )).id
    }

    @AfterEach
    fun deleteCompetition() {
        competitionRepository.deleteCompetition(competitionId)
    }

    @Test
    fun getScheduleMetadata() {
        val scheduleMetadataDTO = scheduleService.getScheduleMetadata(competitionId)
        Assertions.assertNotNull(scheduleMetadataDTO)
        Assertions.assertNotNull(scheduleMetadataDTO.id)
        Assertions.assertNotNull(scheduleMetadataDTO.minutesPerMatch)
        Assertions.assertNotNull(scheduleMetadataDTO.pauseBetweenGroupMatches)
        Assertions.assertNotNull(scheduleMetadataDTO.pauseBetweenPlayoffMatches)
        Assertions.assertNotNull(scheduleMetadataDTO.pauseHoursAfterGroupStage)
    }

    @Test
    fun updateScheduleMetadata() {
        val originalMetadataDTO = scheduleService.getScheduleMetadata(competitionId)
        val newPauseInGroup = 20 // Minutes

        val specToAddd = ScheduleMetadataSpec(
            originalMetadataDTO.minutesPerMatch,
            originalMetadataDTO.pauseHoursAfterGroupStage,
            newPauseInGroup,
            originalMetadataDTO.pauseBetweenPlayoffMatches
        )

        val updatedDTO = scheduleService.updateScheduleMetadata(originalMetadataDTO.id, competitionId, specToAddd)
        Assertions.assertEquals(updatedDTO.minutesPerMatch, originalMetadataDTO.minutesPerMatch)
        Assertions.assertEquals(updatedDTO.pauseHoursAfterGroupStage, originalMetadataDTO.pauseHoursAfterGroupStage)
        Assertions.assertEquals(updatedDTO.pauseBetweenGroupMatches, newPauseInGroup)
        Assertions.assertEquals(updatedDTO.pauseBetweenPlayoffMatches, originalMetadataDTO.pauseBetweenPlayoffMatches)
    }
}