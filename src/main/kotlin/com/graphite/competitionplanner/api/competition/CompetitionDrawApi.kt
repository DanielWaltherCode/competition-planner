package com.graphite.competitionplanner.api.competition

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/competition/{competitionId}/draw/{competitionCategoryId}")
class CompetitionDrawApi {

    @GetMapping
    fun getDraw(@PathVariable competitionCategoryId: Int) {

    }
}