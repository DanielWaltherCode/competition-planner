/* 'user' is a reserved word... */
create table user_table(
    id SERIAL PRIMARY KEY,
    username VARCHAR(50),
    password VARCHAR,
    clubId INTEGER REFERENCES club(id)
);

create table refresh_token(
    id SERIAL PRIMARY KEY,
    refresh_token VARCHAR,
    user_id INTEGER REFERENCES user_table(id) ON DELETE CASCADE NOT NULL
)