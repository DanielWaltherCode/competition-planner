create table schedule_metadata(
    id SERIAL PRIMARY KEY,
    minutes_per_match INTEGER NOT NULL,
    pause_hours_after_group_stage INTEGER default 0,
    pause_between_group_matches INTEGER default 0,
    pause_between_playoff_matches INTEGER default 25, /* Matchens speltid + den här pausen == nästa omgångs tid */
    competition_id INTEGER references competition(id) ON DELETE CASCADE NOT NULL UNIQUE
);

/* The idea is to have one row per hour of competition */
create table schedule_available_tables(
    id SERIAL PRIMARY KEY,
    nr_tables INTEGER not null,
    day DATE,
    hour TIMESTAMP,
    competition_id INTEGER references competition(id) ON DELETE CASCADE NOT NULL,
    UNIQUE(competition_id, day, hour)
);

/**
  * This table could be expanded to include category specific rules for minutes per match, pauses,
  etc. For now, these parameters are set for the competition as a whole.
 */
create table schedule_category(
    id SERIAL PRIMARY KEY,
    start_time TIMESTAMP,
    competiton_category_id INTEGER references competition_category(id) ON DELETE CASCADE NOT NULL UNIQUE
);

create table schedule_daily_times(
    id SERIAL PRIMARY KEY,
    day DATE NOT NULL,
    start_time timestamp NOT NULL,
    end_time timestamp NOT NULL,
    competition_id INTEGER references competition(id) ON DELETE CASCADE NOT NULL,
    UNIQUE(day, competition_id)
)