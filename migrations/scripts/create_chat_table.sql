--liquibase formatted sql

CREATE TABLE IF NOT EXISTS link_tracker_db.Chat (
    id          BIGINT,
    created_at  TIMESTAMPTZ NOT NULL,

    PRIMARY KEY(id)
);
