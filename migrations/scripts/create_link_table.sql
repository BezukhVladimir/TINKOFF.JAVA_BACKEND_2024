--liquibase formatted sql

CREATE SEQUENCE IF NOT EXISTS link_tracker_db.link_id_sequence
    START WITH 1
    INCREMENT BY 10;

CREATE TABLE IF NOT EXISTS link_tracker_db.Link (
    id          BIGINT DEFAULT NEXTVAL('link_tracker_db.link_id_sequence'),
    url         VARCHAR(255) NOT NULL,
    last_update TIMESTAMP WITH TIME ZONE NOT NULL,

    PRIMARY KEY (id),
    UNIQUE (url)
);
