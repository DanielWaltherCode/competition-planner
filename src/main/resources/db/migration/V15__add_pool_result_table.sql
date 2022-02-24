
/**
  * Should only be used to stored final pool result when pool is finished
 */
create table pool_result
(
    id      SERIAL PRIMARY KEY,
    pool_id                     INTEGER REFERENCES pool(id) ON DELETE CASCADE,
    registration_id             INTEGER REFERENCES registration(id) ON DELETE CASCADE,
    pool_position               INTEGER NOT NULL,
    unique(pool_id, pool_position)
);