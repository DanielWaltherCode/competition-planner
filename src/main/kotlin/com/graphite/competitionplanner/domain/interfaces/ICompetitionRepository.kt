package com.graphite.competitionplanner.domain.interfaces

import com.graphite.competitionplanner.domain.dto.CompetitionDTO
import com.graphite.competitionplanner.domain.dto.NewCompetitionDTO

interface ICompetitionRepository {

    /**
     * Stores the Club
     *
     * @param dto Club to be stored
     * @return A new club with identical values as the given dto except the Id
     */
    fun store(dto: NewCompetitionDTO): CompetitionDTO

    /**
     * Find all competitions for the club with the given id
     *
     * @param clubId: A club's id
     * @return A list of all competitions that belongs to the given id
     */
    fun findCompetitionsFor(clubId: Int): List<CompetitionDTO>

    /**
     * Delete the competition with the given Id
     *
     * @param competitionId: A competition's id
     * @return The delete competition
     * @Throws NotFoundException When there is no competition with the given id
     */
    fun delete(competitionId: Int): CompetitionDTO
}