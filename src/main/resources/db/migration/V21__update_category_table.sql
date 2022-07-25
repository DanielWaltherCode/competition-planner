alter table category add column competition_id INTEGER;

alter table category add constraint category_competition_id_fk
    foreign key (competition_id) references competition(id) on delete cascade;