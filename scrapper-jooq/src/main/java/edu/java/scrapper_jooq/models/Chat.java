package edu.java.scrapper_jooq.models;

import java.time.OffsetDateTime;

public record Chat(
    Long id,
    OffsetDateTime createdAt
) {
}
