alter table player add column competition_id integer;

alter table player add constraint player_competition_id_fk
    foreign key (competition_id) references competition(id) on delete cascade;