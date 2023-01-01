package com.graphite.competitionplanner.open.controllers

import com.graphite.competitionplanner.club.interfaces.ClubDTO
import com.graphite.competitionplanner.competition.domain.FindCompetitions
import com.graphite.competitionplanner.competition.interfaces.CompetitionWithClubDTO
import com.graphite.competitionplanner.competition.interfaces.LocationDTO
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import java.time.format.DateTimeFormatter

@Controller
@RequestMapping("")
class RootController(
        val findCompetitions: FindCompetitions
) {

    @GetMapping
    fun index(model: Model): String {
        return "index"
    }
}

data class CompetitionWithDatesAsStringsDTO(
        val id: Int,
        val location: LocationDTO,
        val name: String,
        val welcomeText: String,
        val organizerClub: ClubDTO,
        val startDate: String,
        val endDate: String
)

fun convertCompetitionDTO(competitionWithClubDTO: CompetitionWithClubDTO): CompetitionWithDatesAsStringsDTO {
    return CompetitionWithDatesAsStringsDTO(
            competitionWithClubDTO.id,
            competitionWithClubDTO.location,
            competitionWithClubDTO.name,
            competitionWithClubDTO.welcomeText,
            competitionWithClubDTO.organizerClub,
            competitionWithClubDTO.startDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
            competitionWithClubDTO.endDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    )
}