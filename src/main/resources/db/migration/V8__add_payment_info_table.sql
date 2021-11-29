create table payment_info(
                             id SERIAL PRIMARY KEY,
                             club_id INTEGER REFERENCES club(id) ON DELETE CASCADE,
                             recipient VARCHAR(100),
                             street VARCHAR(100),
                             postcode VARCHAR(10),
                             city VARCHAR(100),
                             plusgiro VARCHAR(100),
                             bankgiro VARCHAR(100),
                             bank_account_nr VARCHAR(100)
);