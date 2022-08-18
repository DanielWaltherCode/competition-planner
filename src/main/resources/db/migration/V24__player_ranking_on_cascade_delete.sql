drop table player_ranking;

create table player_ranking
(
    id                      SERIAL PRIMARY KEY,
    player_id               INTEGER UNIQUE references player (id) ON DELETE CASCADE,
    rank_single INTEGER     NOT NULL DEFAULT 0,
    rank_double INTEGER     NOT NULL DEFAULT 0
);