create table match_schedule
(
    id SERIAL PRIMARY KEY,
    competition_id INTEGER references competition (id) ON DELETE CASCADE NOT NULL,
    start_time TIMESTAMP NOT NULL,
    table_number INTEGER NOT NULL,
    location VARCHAR(30) NOT NULL
);

CREATE UNIQUE INDEX idx_match_schedule
    ON match_schedule(start_time, table_number, location);

alter table match add match_schedule_id int;

alter table match
    add constraint match_match_schedule_id_fk
        foreign key (match_schedule_id) references match_schedule(id);

-- TODO: Drop start- and end-time from match table?