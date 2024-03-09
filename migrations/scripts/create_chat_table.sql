--liquibase formatted sql

CREATE TABLE IF NOT EXISTS link_tracker_db.Chat (
    id          BIGINT NOT NULL,
    created_at  TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id)
);
