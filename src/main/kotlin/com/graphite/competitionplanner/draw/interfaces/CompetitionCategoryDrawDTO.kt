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
    /**
     * This is a mapping between the winners in the pool game to the playoff matches
     */
    var poolToPlayoffMapping: List<GroupToPlayoff> = emptyList()
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
 * A mapping from a position in a group to a play off game
 */
data class GroupToPlayoff(
    /**
     * Id of the playoff match
     */
    val playOffMatchId: Int,
    /**
     * A position in a group that will be matched to player 1
     */
    val player1: GroupPosition,
    /**
     * A position in a group that will be mapped to player 2
     */
    val player2: GroupPosition
)

/*
A draft for mapping a position in a group to a match in playoff

Table description:
id: Primary key
fk_match: Foreign key to match (a playoff match)
match_reg_position: Can be one of two values referencing one of the two columns in the match-tables first_registration or second_registration
fk_pool_draw: Foreign key to a pool
pool_position: Position in the pool that will be placed in the match at match_reg_position

Example:
First row: Winner in group A is mapped to match with ID 1 and set to second_registration
Second row: Second best in group D is mapped to match with ID 1 and set to first_registration
Third row: Second best in group B is mapped to match with ID 2 and set to first_registration

id, fk_match, match_reg_position, fk_pool_draw, pool_position
0,         1,                  2,        "A",               1
0,         1,                  1,        "D",               2
0,         2,                  1,        "B",               2

 */