alter table user_table add column user_role varchar(40);

alter table user_table add constraint unique_user_name UNIQUE(email);