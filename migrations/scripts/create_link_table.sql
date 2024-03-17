--liquibase formatted sql

CREATE TABLE IF NOT EXISTS link_tracker_db.Link (
    id          BIGINT GENERATED ALWAYS AS IDENTITY,
    url         VARCHAR(255) NOT NULL,
    last_update TIMESTAMP WITH TIME ZONE NOT NULL,

    PRIMARY KEY (id),
    UNIQUE (url)
);
