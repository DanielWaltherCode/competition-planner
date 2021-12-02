create table pool
(
    id      SERIAL PRIMARY KEY,
    name    VARCHAR(100) NOT NULL,
    competition_category_id INTEGER references competition_category (id)
);