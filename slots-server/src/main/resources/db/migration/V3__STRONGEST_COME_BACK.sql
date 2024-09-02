CREATE TABLE slots (
                       id SERIAL PRIMARY KEY,
                       start_time TIMESTAMP NOT NULL CHECK (start_time >= NOW()),
                       end_time TIMESTAMP NOT NULL CHECK (end_time >= NOW()),
                       booked BOOLEAN DEFAULT FALSE,
                       booked_by VARCHAR(100),
                       employee_name VARCHAR(100)
);
