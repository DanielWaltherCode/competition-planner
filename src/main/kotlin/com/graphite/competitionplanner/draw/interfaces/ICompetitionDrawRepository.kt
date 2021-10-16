package com.graphite.competitionplanner.draw.interfaces

import com.graphite.competitionplanner.draw.domain.CompetitionCategoryDrawSpec

interface ICompetitionDrawRepository {

    fun store(draw: CompetitionCategoryDrawSpec)
}