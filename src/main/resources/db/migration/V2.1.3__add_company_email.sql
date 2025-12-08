-- +FlywayNonTransactional

-- Add email column to company table
ALTER TABLE company
    ADD COLUMN email varchar(255) DEFAULT NULL;

