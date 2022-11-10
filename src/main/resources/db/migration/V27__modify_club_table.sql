alter table club add column competition_id integer;

alter table club add constraint club_competition_id_fk
    foreign key (competition_id) references competition(id) on delete cascade;

alter table club drop constraint if exists club_name_key;

alter table club add constraint club_competition_name_key UNIQUE(name, competition_id);