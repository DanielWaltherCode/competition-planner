package com.graphite.competitionplanner.draw.interfaces

import com.graphite.competitionplanner.match.service.MatchAndResultDTO
import com.graphite.competitionplanner.player.interfaces.PlayerWithClubDTO

/**
 * A description of all matches in a competition category
 */
data class CompetitionCategoryDrawDTO(
    /**
     * ID of the competition category
     */
    val competitionCategoryId: Int,
    /**
     * List of matches played in play off
     */
    val playOff: List<PlayoffRoundDTO>,
    /**
     * List of groups including players and matches
     */
    val groups: List<GroupDrawDTO>,

    /**
     * Shows link between position in group and position in playoff
     */
    val poolToPlayoffMap: List<GroupToPlayoff>
)

/**
 * Playoff match rounds to return to website
 */
data class PlayoffRoundDTO(
    val round: Round,
    val matches: List<MatchAndResultDTO>
)

/**
 * A description of a group, including name, players, and matches.
 */
data class GroupDrawDTO(
    /**
     * Name of the group
     */
    val name: String,
    /**
     * List of players in this group. Each sublist contains two players if it's a doubles class
     */
    val players: List<PlayerInPoolDTO>,
    /**
     * Matches in this group
     */
    val matches: List<MatchAndResultDTO>,

    val groupStandingList: List<GroupStandingDTO>,

    val subGroupList: List<SubGroupContainer>
)

data class PlayerInPoolDTO(
    val playerDTOs: List<PlayerWithClubDTO>,
    val playerNumber: Int
)

data class SubGroupContainer(
    val groupScore: Int,
    val groupStandingList: List<GroupStandingDTO>
)


/**
 * A position in a group
 */
data class GroupPosition(
    /**
     * Name of group
     */
    val groupName: String,
    /**
     * Position in group. Winner is position 1, second best is position 2, etc
     */
    val position: Int
)

/**
 * A position in playoff
 */
data class PlayoffPosition(
    /**
     * ID of the playoff match
     */
    val matchId: Int,

    /**
     * Player1 or Player2
     */
    val position: Int
)

/**
 * A mapping between a position in a group to a position in playoff
 */
data class GroupToPlayoff(
    /**
     * A position in the playoff
     */
    val playoffPosition: PlayoffPosition,
    /**
     * A position in a group
     */
    val groupPosition: GroupPosition
)