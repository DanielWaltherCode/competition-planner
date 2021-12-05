package com.graphite.competitionplanner.draw.interfaces

import com.graphite.competitionplanner.competitioncategory.entity.Round
import com.graphite.competitionplanner.player.interfaces.PlayerWithClubDTO

/**
 * A description of all matches in a competition category
 */
data class CompetitionCategoryDrawDTO(
    /**
     * Id of the competition category
     */
    val competitionCategoryId: Int,
    /**
     * List of matches played in play off
     */
    val playOff: List<PlayOffMatchDTO>,
    /**
     * List of groups including players and matches
     */
    val groupDraw: List<GroupDrawDTO>,
)

/**
 * A match played in playoff
 */
data class PlayOffMatchDTO(
    /**
     * Match id
     */
    val id: Int,
    /**
     * List of players in team one. Contains two players if it is a double game
     */
    val player1: List<PlayerWithClubDTO>,
    /**
     * List of players in team two. Contains two players if it is a double game
     */
    val player2: List<PlayerWithClubDTO>,
    /**
     * Match order. Starts at 1 and ends at the number equal to the total matches for this round.
     *
     * Important: Winners from matches with order 1 and 2, end up playing each other i the next round. The same goes
     * for 3 and 4, 5 and 6, etc.
     */
    var order: Int,
    /**
     * The round this match is played in.
     */
    var round: Round,
    /**
     * The winner of this match. If list is is empty, then no winner has been decided. Contains two players if it is a
     * double game
     */
    val winner: List<PlayerWithClubDTO>,
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
     * List of players in this group
     */
    val players: List<PlayerWithClubDTO>,
    /**
     * Matches in this group
     */
    val matches: List<GroupMatchDTO>
)

/**
 * A match player in the group stage
 */
data class GroupMatchDTO(
    /**
     * Match id
     */
    val id: Int,
    /**
     * List of players in team one. Contains two players if it is a double game
     */
    val player1: List<PlayerWithClubDTO>,
    /**
     * List of players in team two. Contains two players if it is a double game
     */
    val player2: List<PlayerWithClubDTO>,
    /**
     * The winner of this match. If list is is empty, then no winner has been decided. Contains two players if it is a
     * double game
     */
    val winner: List<PlayerWithClubDTO>
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