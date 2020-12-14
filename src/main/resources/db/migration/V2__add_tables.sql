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
                                               competition_playing_category_id INTEGER references competition_playing_category(id) ON DELETE CASCADE,
                                               cost REAL,
                                               start_time TIMESTAMP,
                                               draw_type_id INTEGER references draw_type(id),
                                               nr_players_per_group INTEGER,
                                               nr_players_to_playoff INTEGER,
                                               pool_draw_strategy_id INTEGER references pool_draw_strategy(id)
);