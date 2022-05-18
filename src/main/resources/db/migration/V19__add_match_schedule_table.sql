create table match_time_slot
(
    id SERIAL PRIMARY KEY,
    competition_id INTEGER references competition (id) ON DELETE CASCADE NOT NULL,
    start_time TIMESTAMP NOT NULL,
    table_number INTEGER NOT NULL,
    location VARCHAR(30) NOT NULL
);

CREATE UNIQUE INDEX idx_match_time_slot
    ON match_time_slot(start_time, table_number, location);

alter table match add match_time_slot_id int;

alter table match
    add constraint match_match_time_slot_id_fk
        foreign key (match_time_slot_id) references match_time_slot(id);