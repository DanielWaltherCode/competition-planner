package com.graphite.competitionplanner.service

import com.graphite.competitionplanner.Tables.COMPETITION
import com.graphite.competitionplanner.api.ClubNoAddressDTO
import com.graphite.competitionplanner.repositories.ClubRepository
import com.graphite.competitionplanner.repositories.RegistrationRepository
import com.graphite.competitionplanner.repositories.competition.CompetitionCategoryRepository
import com.graphite.competitionplanner.repositories.competition.CompetitionRepository
import com.graphite.competitionplanner.tables.Club.CLUB
import com.graphite.competitionplanner.tables.records.ClubRecord
import com.graphite.competitionplanner.tables.records.CompetitionRecord
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDate

@Service
class CompetitionService(
    val competitionRepository: CompetitionRepository, val clubRepository: ClubRepository,
    val competitionCategoryRepository: CompetitionCategoryRepository,
    val registrationRepository: RegistrationRepository
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


    fun addCompetition(competitionDTO: CompetitionDTO): CompetitionDTO {
        val competition = competitionRepository.addCompetition(competitionDTO)
        val club = clubRepository.getById(competition.organizingClub) ?: throw ResponseStatusException(
            HttpStatus.NOT_FOUND,
            "No club with id ${competition.organizingClub} found"
        )
        return recordsToDto(competition, club)
    }

    fun updateCompetition(competitionDTO: CompetitionDTO): CompetitionDTO {
        val competition = competitionRepository.updateCompetition(competitionDTO)
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

}


fun recordsToDto(competition: CompetitionRecord, club: ClubRecord): CompetitionDTO {
    return CompetitionDTO(
        competition.id, competition.location, competition.welcomeText,
        ClubNoAddressDTO(club.id, club.name), competition.startDate, competition.endDate
    )
}

data class CompetitionDTO(
    val id: Int?,
    val location: String,
    val welcomeText: String,
    val organizingClub: ClubNoAddressDTO,
    val startDate: LocalDate,
    val endDate: LocalDate
)

