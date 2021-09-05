create table competition_category_metadata (
                                               id SERIAL PRIMARY KEY,
                                               competition_category_id INTEGER references competition_category(id) ON DELETE CASCADE NOT NULL UNIQUE,
                                               cost REAL,
                                               draw_type VARCHAR(50),
                                               nr_players_per_group INTEGER,
                                               nr_players_to_playoff INTEGER,
                                               pool_draw_strategy VARCHAR(50)
);

create table competition_category_game_rules(
    id SERIAL PRIMARY KEY,
    competition_category_id INTEGER references competition_category(id) ON DELETE CASCADE NOT NULL UNIQUE,
    nr_sets INTEGER NOT NULL,
    win_score INTEGER NOT NULL,
    win_margin INTEGER,
    different_number_of_games_from_round VARCHAR(40),
    nr_sets_final INTEGER NOT NULL,
    win_score_final INTEGER,
    win_margin_final INTEGER,
    tie_break_in_final_game BOOLEAN,
    win_score_tiebreak INTEGER,
    win_margin_tie_break INTEGER
);

