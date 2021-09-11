package com.graphite.competitionplanner.competition.service

import com.fasterxml.jackson.annotation.JsonFormat
import com.graphite.competitionplanner.Tables.COMPETITION
import com.graphite.competitionplanner.club.interfaces.ClubDTO
import com.graphite.competitionplanner.club.interfaces.ClubNoAddressDTO
import com.graphite.competitionplanner.club.repository.ClubRepository
import com.graphite.competitionplanner.club.service.ClubService
import com.graphite.competitionplanner.competition.api.CompetitionSpec
import com.graphite.competitionplanner.competition.repository.CompetitionRepository
import com.graphite.competitionplanner.competitioncategory.repository.CompetitionCategoryRepository
import com.graphite.competitionplanner.registration.service.CompetitionCategoryDTO
import com.graphite.competitionplanner.schedule.api.AvailableTablesWholeCompetitionSpec
import com.graphite.competitionplanner.schedule.service.ScheduleService
import com.graphite.competitionplanner.tables.Club.CLUB
import com.graphite.competitionplanner.tables.records.CompetitionRecord
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class CompetitionService(
    val competitionRepository: CompetitionRepository, val clubRepository: ClubRepository,
    val competitionCategoryRepository: CompetitionCategoryRepository,
    val scheduleService: ScheduleService,
    val clubService: ClubService
) {

    fun getCompetitions(weekStartDate: LocalDate?, weekEndDate: LocalDate?): List<CompetitionDTO> {
        val competitionsAndClubs = competitionRepository.getCompetitions(weekStartDate, weekEndDate)
        val competitionDTOs = mutableListOf<CompetitionDTO>()
        for (entry in competitionsAndClubs) {
            val competitionRecord = entry.into(COMPETITION)
            val club = entry.into(CLUB)
            competitionDTOs.add(recordsToDto(competitionRecord, ClubDTO(club.id, club.name, club.address)))
        }
        return competitionDTOs
    }

    fun getCategoriesInCompetition(competitionId: Int): List<CompetitionCategoryDTO> {
        val competitionCategories = competitionCategoryRepository.getCategoriesInCompetition(competitionId)
        return competitionCategories.map { CompetitionCategoryDTO(it.categoryId, it.categoryName) }
    }


    fun addCompetition(competitionSpec: CompetitionSpec): CompetitionDTO {
        val competition = competitionRepository.addCompetition(competitionSpec)
        // Add default competition schedule metadata
        scheduleService.addDefaultScheduleMetadata(competition.id)
        scheduleService.addDailyStartAndEndForWholeCompetition(competition.id)
        val club = clubService.findById(competition.organizingClub)
        scheduleService.registerTablesAvailableForWholeCompetition(competition.id, AvailableTablesWholeCompetitionSpec(0))
        return recordsToDto(competition, club)
    }

    fun updateCompetition(competitionId: Int, competitionSpec: CompetitionSpec): CompetitionDTO {
        val competition = competitionRepository.updateCompetition(competitionId, competitionSpec)
        val club = clubService.findById(competition.organizingClub)
        return recordsToDto(competition, club)
    }

    fun getById(competitionId: Int): CompetitionDTO {
        val record = competitionRepository.getById(competitionId)
        val competition = record.into(COMPETITION)
        val club = record.into(CLUB)
        return recordsToDto(competition, ClubDTO(club.id, club.name, club.address))
    }

    fun getByClubId(clubId: Int): List<CompetitionDTO> {
        val competitionRecords = competitionRepository.getByClubId(clubId)
        val competitionDTOs = mutableListOf<CompetitionDTO>()

        for (record in competitionRecords) {
            val competition = record.into(COMPETITION)
            val club = record.into(CLUB)
            competitionDTOs.add(recordsToDto(competition, ClubDTO(club.id, club.name, club.address)))
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


fun recordsToDto(competition: CompetitionRecord, club: ClubDTO): CompetitionDTO {
    return CompetitionDTO(
        competition.id, competition.name, competition.location, competition.welcomeText,
        ClubNoAddressDTO(club.id, club.name), competition.startDate, competition.endDate
    )
}

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

