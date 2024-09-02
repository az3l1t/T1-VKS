ALTER TABLE slots
    DROP COLUMN employeeEmail;

ALTER TABLE slots
    ADD COLUMN employee_email VARCHAR(100);
