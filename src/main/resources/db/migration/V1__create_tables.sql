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
    welcome_text    VARCHAR      NOT NULL,
    organizing_club INTEGER references club(id),
    start_date      DATE,
    end_date        DATE
);

create table playing_category
(
    id            SERIAL PRIMARY KEY,
    category_name VARCHAR NOT NULL
);

create table competition_playing_category
(
    id               SERIAL PRIMARY KEY,
    competition_id   INTEGER references competition (id),
    playing_category INTEGER references playing_category (id)
);


create table registration
(
    id                SERIAL PRIMARY KEY,
    registration_date DATE DEFAULT CURRENT_DATE
);

create table player_registration
(
    id              SERIAL PRIMARY KEY,
    registration_id INTEGER REFERENCES registration (id),
    player_id       INTEGER REFERENCES player (id)
);

create table playing_in
(
    id                              SERIAL PRIMARY KEY,
    registration_id                 INTEGER REFERENCES registration (id),
    seed                            INTEGER,
    competition_playing_category_id INTEGER references competition_playing_category (id)

);

create table match
(
    id                              SERIAL PRIMARY KEY,
    start_time                      TIMESTAMP NOT NULL,
    end_time                        TIMESTAMP NOT NULL,
    competition_playing_category_id INTEGER references competition_playing_category (id),
    round                           VARCHAR   NOT NULL,
    first_registration_id           INTEGER REFERENCES registration (id),
    second_registration_id          INTEGER REFERENCES registration (id)
);


create table result
(
    id       SERIAL PRIMARY KEY,
    match_id INTEGER REFERENCES match (id),
    winner   INTEGER NOT NULL
);

create table set
(
    id                         SERIAL PRIMARY KEY,
    set_number                 INTEGER NOT NULL,
    first_registration_result  INTEGER NOT NULL,
    second_registration_result INTEGER NOT NULL,
    match_id INTEGER references match(id) ON DELETE CASCADE NOT NULL
)