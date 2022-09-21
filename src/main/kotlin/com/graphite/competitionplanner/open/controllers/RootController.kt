package com.graphite.competitionplanner.open.controllers

import com.fasterxml.jackson.annotation.JsonFormat
import com.graphite.competitionplanner.club.interfaces.ClubDTO
import com.graphite.competitionplanner.competition.domain.FindCompetitions
import com.graphite.competitionplanner.competition.interfaces.CompetitionWithClubDTO
import com.graphite.competitionplanner.competition.interfaces.LocationDTO
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Controller
@RequestMapping("open")
class RootController(
        val findCompetitions: FindCompetitions
) {

    @GetMapping
    fun index(model: Model,
              @RequestParam(required = false, defaultValue = "CURRENT") searchPeriod: SearchPeriod): String {
        val startDate: LocalDate
        val endDate: LocalDate
        val today: LocalDate = LocalDate.now()
        if (searchPeriod == SearchPeriod.PREVIOUS) {
            startDate = today.minusDays(90)
            endDate = today.minusDays(today.dayOfWeek.value.toLong()) // End of last week
        }
        else if (searchPeriod == SearchPeriod.CURRENT) {
            startDate = today.minusDays(today.dayOfWeek.value.toLong() -1)
            endDate = today.plusDays((7 - today.dayOfWeek.value.toLong()))
        }
        else { // Coming competitions
            startDate = today.plusDays((7 - today.dayOfWeek.value.toLong() + 1))
            endDate = today.plusDays(60)
        }
        val competitions = findCompetitions.thatStartOrEndWithin(startDate, endDate)
        model.addAttribute("competitions", competitions.map { convertCompetitionDTO(it) })
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