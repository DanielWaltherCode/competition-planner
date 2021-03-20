
create table pool_draw(
                          id SERIAL PRIMARY KEY,
                          registration_id INTEGER REFERENCES registration(id),
                          competition_category_id INTEGER REFERENCES competition_category(id),
                          group_name VARCHAR(10),
                          player_number INTEGER /* The position this player has in the pool */
)