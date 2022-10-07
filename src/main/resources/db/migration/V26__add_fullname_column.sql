alter table player add column full_name varchar;
update player set full_name = concat(first_name, ' ', last_name);