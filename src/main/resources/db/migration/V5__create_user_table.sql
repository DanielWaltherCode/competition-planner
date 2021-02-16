/* 'user' is a reserved word... */
create table user_table(
    id SERIAL PRIMARY KEY,
    username VARCHAR(50),
    password VARCHAR,
    clubId INTEGER REFERENCES club(id)
)