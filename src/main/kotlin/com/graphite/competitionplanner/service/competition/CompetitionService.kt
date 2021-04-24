package com.graphite.competitionplanner.service.competition

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonFormat
import com.graphite.competitionplanner.Tables.COMPETITION
import com.graphite.competitionplanner.api.AvailableTablesWholeCompetitionSpec
import com.graphite.competitionplanner.api.ClubNoAddressDTO
import com.graphite.competitionplanner.api.competition.CompetitionSpec
import com.graphite.competitionplanner.repositories.ClubRepository
import com.graphite.competitionplanner.repositories.competition.CompetitionCategoryRepository
import com.graphite.competitionplanner.repositories.competition.CompetitionRepository
import com.graphite.competitionplanner.service.CompetitionAndCategoriesDTO
import com.graphite.competitionplanner.service.CompetitionCategoryDTO
import com.graphite.competitionplanner.service.ScheduleService
import com.graphite.competitionplanner.tables.Club.CLUB
import com.graphite.competitionplanner.tables.records.ClubRecord
import com.graphite.competitionplanner.tables.records.CompetitionRecord
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.time.Duration
import java.time.LocalDate

@Service
class CompetitionService(
    val competitionRepository: CompetitionRepository, val clubRepository: ClubRepository,
    val competitionCategoryRepository: CompetitionCategoryRepository,
    val scheduleService: ScheduleService
) {

    fun getCompetitions(weekStartDate: LocalDate?, weekEndDate: LocalDate?): List<CompetitionDTO> {
        val competitionsAndClubs = competitionRepository.getCompetitions(weekStartDate, weekEndDate)
        val competitionDTOs = mutableListOf<CompetitionDTO>()
        for (entry in competitionsAndClubs) {
            val competitionRecord = entry.into(COMPETITION)
            val clubRecord = entry.into(CLUB)
            competitionDTOs.add(recordsToDto(competitionRecord, clubRecord))
        }
        return competitionDTOs
    }

    fun getCategoriesInCompetition(competitionId: Int): CompetitionAndCategoriesDTO {
        val competitionCategories = competitionCategoryRepository.getCategoriesInCompetition(competitionId)
        return CompetitionAndCategoriesDTO(
            getById(competitionId),
            competitionCategories.map { CompetitionCategoryDTO(it.categoryId, it.categoryName) })
    }


    fun addCompetition(competitionSpec: CompetitionSpec): CompetitionDTO {
        val competition = competitionRepository.addCompetition(competitionSpec)
        // Add default competition schedule metadata
        scheduleService.addDefaultScheduleMetadata(competition.id)
        scheduleService.addDailyStartAndEndForWholeCompetition(competition.id)
        val club = clubRepository.getById(competition.organizingClub) ?: throw ResponseStatusException(
            HttpStatus.NOT_FOUND,
            "No club with id ${competition.organizingClub} found"
        )
        scheduleService.registerTablesAvailableForWholeCompetition(competition.id, AvailableTablesWholeCompetitionSpec(0))
        return recordsToDto(competition, club)
    }

    fun updateCompetition(competitionId: Int, competitionSpec: CompetitionSpec): CompetitionDTO {
        val competition = competitionRepository.updateCompetition(competitionId, competitionSpec)
        val club = clubRepository.getById(competition.organizingClub) ?: throw ResponseStatusException(
            HttpStatus.NOT_FOUND,
            "No club with id ${competition.organizingClub} found"
        )
        return recordsToDto(competition, club)
    }

    fun getById(competitionId: Int): CompetitionDTO {
        val record = competitionRepository.getById(competitionId)
        val competition = record.into(COMPETITION)
        val club = record.into(CLUB)
        return recordsToDto(competition, club)
    }

    fun getByClubId(clubId: Int): List<CompetitionDTO> {
        val competitionRecords = competitionRepository.getByClubId(clubId)
        val competitionDTOs = mutableListOf<CompetitionDTO>()

        for (record in competitionRecords) {
            val competition = record.into(COMPETITION)
            val club = record.into(CLUB)
            competitionDTOs.add(recordsToDto(competition, club))
        }
        return competitionDTOs
    }

    fun getDaysOfCompetition(competitionId: Int): List<LocalDate> {
        val competition = getById(competitionId)
        val dates = mutableListOf<LocalDate>()

        if (competition.startDate != null && competition.endDate != null) {
            var currentDate = competition.startDate

            while (currentDate!! <= competition.endDate) {
                dates.add(LocalDate.from(currentDate))
                currentDate = currentDate.plusDays(1)
            }
        }
        return dates
    }

}


fun recordsToDto(competition: CompetitionRecord, club: ClubRecord): CompetitionDTO {
    return CompetitionDTO(
        competition.id, competition.name, competition.location, competition.welcomeText,
        ClubNoAddressDTO(club.id, club.name), competition.startDate, competition.endDate
    )
}

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
data class CompetitionDTO(
    val id: Int,
    val name: String,
    val location: String,
    val welcomeText: String?,
    val organizingClub: ClubNoAddressDTO,
    @JsonFormat(pattern = "yyyy-MM-dd")
    val startDate: LocalDate?,
    @JsonFormat(pattern = "yyyy-MM-dd")
    val endDate: LocalDate?
)

data class CompetitionDays(
    @JsonFormat(pattern = "yyyy-MM-dd")
    val competitionDays: List<LocalDate>
)

