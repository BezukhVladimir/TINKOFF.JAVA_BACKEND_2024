package edu.java.scrapper.models;

import java.time.OffsetDateTime;

public record Chat(
    Long id,
    OffsetDateTime createdAt
) {
}
