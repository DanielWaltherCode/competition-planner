create table pre_schedule
(
    competition_id INTEGER references competition(id) ON DELETE CASCADE NOT NULL,
    play_date DATE NOT NULL,
    time_interval VARCHAR(30) NOT NULL,
    competition_category_id INTEGER references competition_category(id) ON DELETE CASCADE NOT NULL,
    estimated_end_time TIMESTAMP,
    success BOOLEAN DEFAULT true
);

CREATE UNIQUE INDEX idx_pre_schedule
    ON pre_schedule(competition_id, play_date, time_interval, competition_category_id);