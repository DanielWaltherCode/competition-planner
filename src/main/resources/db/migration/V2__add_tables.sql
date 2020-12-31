create table draw_type(
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

create table pool_draw_strategy(
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

create table competition_category_metadata (
                                               id SERIAL PRIMARY KEY,
                                               competition_category_id INTEGER references competition_category(id) ON DELETE CASCADE NOT NULL UNIQUE,
                                               cost REAL,
                                               start_time TIMESTAMP,
                                               draw_type_id INTEGER references draw_type(id),
                                               nr_players_per_group INTEGER,
                                               nr_players_to_playoff INTEGER,
                                               pool_draw_strategy_id INTEGER references pool_draw_strategy(id)
);

create table competition_category_game_rules(
    id SERIAL PRIMARY KEY,
    competition_category_id INTEGER references competition_category(id) ON DELETE CASCADE NOT NULL UNIQUE,
    nr_sets INTEGER NOT NULL,
    win_score INTEGER NOT NULL,
    win_margin INTEGER,
    nr_sets_final INTEGER NOT NULL,
    win_score_final INTEGER,
    win_margin_final INTEGER,
    win_score_tiebreak INTEGER,
    win_margin_tie_break INTEGER
);

