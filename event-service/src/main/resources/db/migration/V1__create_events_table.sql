-- V1__create_events_table.sql
-- Initial schema: creates the events table with all required columns.

CREATE TABLE IF NOT EXISTS events (
    id          VARCHAR(20)    NOT NULL,
    title       VARCHAR(150)   NOT NULL,
    description VARCHAR(500)   NOT NULL,
    date        VARCHAR(255)   NOT NULL,
    location    VARCHAR(200)   NOT NULL,
    base_price  DECIMAL(12, 2) NOT NULL,
    deleted     BOOLEAN        NOT NULL DEFAULT FALSE,
    created_at  DATETIME       NOT NULL,
    updated_at  DATETIME,
    PRIMARY KEY (id)
);
