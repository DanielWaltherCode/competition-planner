create table club
(
    id      SERIAL PRIMARY KEY,
    name    VARCHAR(100) NOT NULL UNIQUE,
    address VARCHAR      NOT NULL
);

create table player
(
    id            SERIAL PRIMARY KEY,
    first_name    VARCHAR NOT NULL,
    last_name     VARCHAR NOT NULL,
    club_id       INTEGER references club(id),
    date_of_birth DATE    NOT NULL
);

create table competition
(
    id              SERIAL PRIMARY KEY,
    location        VARCHAR(100) NOT NULL,
    name            VARCHAR NOT NULL,
    welcome_text    VARCHAR,
    organizing_club INTEGER references club(id),
    start_date      DATE,
    end_date        DATE
);

create table category
(
    id            SERIAL PRIMARY KEY,
    category_name VARCHAR NOT NULL UNIQUE,
    category_type VARCHAR NOT NULL /* Singles or doubles */
);

create table competition_category
(
    id               SERIAL PRIMARY KEY,
    competition_id   INTEGER references competition (id),
    category INTEGER references category (id),
    status VARCHAR(50) DEFAULT 'ACTIVE',
    unique (competition_id, category)
);

create table registration
(
    id                SERIAL PRIMARY KEY,
    registration_date DATE DEFAULT CURRENT_DATE
);

create table player_registration
(
    id              SERIAL PRIMARY KEY,
    registration_id INTEGER REFERENCES registration (id) ON DELETE CASCADE,
    player_id       INTEGER REFERENCES player (id)
);

create table competition_category_registration
(
    id                              SERIAL PRIMARY KEY,
    registration_id                 INTEGER REFERENCES registration (id) ON DELETE CASCADE,
    seed                            INTEGER,
    competition_category_id INTEGER references competition_category (id),
    unique (registration_id, competition_category_id)
);

create table match
(
    id                              SERIAL PRIMARY KEY,
    start_time                      TIMESTAMP,
    end_time                        TIMESTAMP,
    competition_category_id INTEGER references competition_category (id),
    match_type                           VARCHAR   NOT NULL, /* Either Group or Playoff */
    first_registration_id           INTEGER REFERENCES registration (id),
    second_registration_id          INTEGER REFERENCES registration (id),
    match_order_number INTEGER NOT NULL,
    group_or_round VARCHAR(30), /* Either which group or which round */
    winner INTEGER references registration(id)
);


create table result
(
    id       SERIAL PRIMARY KEY,
    match_id INTEGER REFERENCES match(id),
    winner   INTEGER NOT NULL
);

create table game
(
    id                         SERIAL PRIMARY KEY,
    game_number                 INTEGER NOT NULL,
    first_registration_result  INTEGER NOT NULL,
    second_registration_result INTEGER NOT NULL,
    match_id INTEGER references match (id) ON DELETE CASCADE NOT NULL,
    unique (game_number, match_id)
);