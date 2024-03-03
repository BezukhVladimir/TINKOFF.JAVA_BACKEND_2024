--liquibase formatted sql

CREATE TABLE IF NOT EXISTS link_tracker_db.Link (
    id          BIGINT       NOT NULL,
    url         VARCHAR(255) NOT NULL,
    last_update TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id)
);
