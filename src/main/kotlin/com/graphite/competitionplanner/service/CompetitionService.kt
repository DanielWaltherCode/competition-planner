package com.graphite.competitionplanner.service

import com.graphite.competitionplanner.Tables.COMPETITION
import com.graphite.competitionplanner.api.ClubNoAddressDTO
import com.graphite.competitionplanner.repositories.ClubRepository
import com.graphite.competitionplanner.repositories.CompetitionAndPlayingCategoryRepository
import com.graphite.competitionplanner.repositories.CompetitionRepository
import com.graphite.competitionplanner.repositories.RegistrationRepository
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
    val competitionAndPlayingCategoryRepository: CompetitionAndPlayingCategoryRepository,
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
        val competitionCategories = competitionAndPlayingCategoryRepository.getCategoriesInCompetition(competitionId)
        return CompetitionAndCategoriesDTO(
            getById(competitionId),
            competitionCategories.map { Category(it.playingCategoryId, it.categoryName) })
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

    /**
     * Cancel competition category. This is used when players have already
     * signed up for category. Then we shouldn't delete data but just the category to
     * cancelled
     */
    fun cancelCategoryInCompetition(playingCategoryId: Int) {
        competitionAndPlayingCategoryRepository.cancelCategoryInCompetition(playingCategoryId)
    }

    /**
     * Categories can be deleted if no players are registered yet
     * Delete from competition categories
     * Competition category metadata should be deleted on cascade
     */
    fun deleteCategoryInCompetition(playingCategoryId: Int) {
        // Check if playing_in contains this id, if so don't delete
        if (registrationRepository.checkIfCategoryHasRegistrations(playingCategoryId)) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "A category with registered players should not be deleted")
        }
        competitionAndPlayingCategoryRepository.deleteCategoryInCompetition(playingCategoryId)
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
