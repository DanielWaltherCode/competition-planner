package com.graphite.competitionplanner.competition.interfaces

import com.graphite.competitionplanner.common.exception.NotFoundException
import java.time.LocalDate

interface ICompetitionRepository {

    /**
     * Stores a new club based on the given specification
     *
     * @param spec Specification of the club
     * @return The newly stored club
     */
    fun store(spec: CompetitionSpec): CompetitionDTO

    /**
     * Find all competitions for the club with the given id
     *
     * @param clubId: A club's id
     * @return A list of all competitions that belongs to the given id
     */
    fun findCompetitionsThatBelongTo(clubId: Int): List<CompetitionWithClubDTO>

    /***
     * Return all competitions that are within the given dates
     */
    fun findCompetitions(start: LocalDate, end: LocalDate): List<CompetitionWithClubDTO>

    /**
     * Return the competition with the given id
     *
     * @param competitionId: Id of the competition
     * @return The competition
     * @throws NotFoundException - When a competition with the given id cannot be found
     */
    @Throws(NotFoundException::class)
    fun findById(competitionId: Int): CompetitionDTO

    /**
     * Delete the competition with the given Id
     *
     * @param competitionId: A competition's id
     * @return The delete competition
     * @Throws NotFoundException When there is no competition with the given id
     */
    @Throws(NotFoundException::class)
    fun delete(competitionId: Int): CompetitionDTO

    /**
     * Updated the given competition
     *
     * @param id: Id of the competition
     * @param spec: The new specification
     * @Throws NotFoundException When the competition cannot be found
     */
    fun update(id: Int, spec: CompetitionUpdateSpec): CompetitionDTO
}