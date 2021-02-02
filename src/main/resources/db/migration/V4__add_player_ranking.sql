create table player_ranking
(
    id                      SERIAL PRIMARY KEY,
    player_id               INTEGER references player (id),
    rank_single INTEGER     NOT NULL DEFAULT 0,
    rank_double INTEGER     NOT NULL DEFAULT 0
);