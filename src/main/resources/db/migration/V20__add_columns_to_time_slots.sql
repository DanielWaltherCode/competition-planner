alter table match_time_slot add column competition_category_id INTEGER;

alter table match_time_slot add constraint ccid_match_time_slot_id_fk
    foreign key (competition_category_id) references competition_category(id);

alter table match_time_slot add column match_type VARCHAR(20);
