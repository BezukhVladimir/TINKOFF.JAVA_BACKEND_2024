--liquibase formatted sql

CREATE TABLE IF NOT EXISTS link_tracker_db.Chats_Links (
    id_chat BIGINT NOT NULL REFERENCES link_tracker_db.Chat (id) ON DELETE CASCADE,
    id_link BIGINT NOT NULL REFERENCES link_tracker_db.Link (id) ON DELETE CASCADE,

    PRIMARY KEY (id_chat, id_link)
);
