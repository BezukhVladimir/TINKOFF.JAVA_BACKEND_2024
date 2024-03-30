--liquibase formatted sql

CREATE TABLE IF NOT EXISTS link_tracker_db.Chat (
    id          BIGINT NOT NULL,
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL,

    PRIMARY KEY(id)
);
