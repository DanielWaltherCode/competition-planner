alter table public.club
    drop constraint if exists club_competition_name_key cascade;

CREATE UNIQUE INDEX club_competition_unique_idx ON club (name, competition_id)
    WHERE club.competition_id IS NOT NULL;

CREATE UNIQUE INDEX club_unique_idx ON club (name)
    WHERE club.competition_id IS NULL;