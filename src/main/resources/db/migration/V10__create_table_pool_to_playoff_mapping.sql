create table pool_to_playoff_map
(
    id                          SERIAL PRIMARY KEY,
    competition_category_id     INTEGER REFERENCES competition_category(id),
    match_id                    INTEGER REFERENCES match(id),
    match_registration_position INTEGER NOT NULL,
    pool_id                     INTEGER REFERENCES pool(id),
    pool_position               INTEGER NOT NULL
);