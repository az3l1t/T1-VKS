ALTER TABLE _user
    ADD COLUMN first_name VARCHAR(50) NOT NULL,
    ADD COLUMN last_name VARCHAR(50) NOT NULL,
    ADD COLUMN phone VARCHAR(15) NOT NULL UNIQUE;