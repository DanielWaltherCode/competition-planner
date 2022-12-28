package com.graphite.competitionplanner.admin

import com.graphite.competitionplanner.club.domain.CreateClub
import com.graphite.competitionplanner.club.interfaces.ClubDTO
import com.graphite.competitionplanner.club.interfaces.ClubSpec
import com.graphite.competitionplanner.competition.domain.FindCompetitions
import com.graphite.competitionplanner.competition.interfaces.CompetitionWithClubDTO
import com.graphite.competitionplanner.user.api.UserSpec
import com.graphite.competitionplanner.user.service.UserDTO
import com.graphite.competitionplanner.user.service.UserService
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import javax.validation.Valid

@RestController
@RequestMapping("admin")
class AdminController(
        val createClub: CreateClub,
        val userService: UserService,
        val findCompetitions: FindCompetitions
) {

    @PostMapping("/club")
    fun addClub(@RequestBody @Valid clubSpec: ClubSpec): ClubDTO {
        return createClub.execute(clubSpec)
    }

    @PostMapping("/user")
    fun registerUser(@RequestBody @Valid userSpec: UserSpec): UserDTO {
        return userService.addUser(userSpec)
    }

    @GetMapping("/competitions")
    fun getCompetitions(): List<CompetitionWithClubDTO> {
        val startDate = LocalDate.now().minusMonths(2)
        val endDate = LocalDate.now().plusWeeks(2)
        return findCompetitions.thatStartOrEndWithin(startDate, endDate)
    }
}