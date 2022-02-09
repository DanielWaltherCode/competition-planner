drop table pool_to_playoff_map;
drop table pool;

create table pool
(
    id      SERIAL PRIMARY KEY,
    name    VARCHAR(100) NOT NULL,
    competition_category_id INTEGER references competition_category (id) ON DELETE CASCADE
);

create table pool_to_playoff_map
(
    id                          SERIAL PRIMARY KEY,
    competition_category_id     INTEGER REFERENCES competition_category(id) ON DELETE CASCADE,
    match_id                    INTEGER REFERENCES match(id) ON DELETE CASCADE,
    match_registration_position INTEGER NOT NULL,
    pool_id                     INTEGER REFERENCES pool(id) ON DELETE CASCADE,
    pool_position               INTEGER NOT NULL
);

