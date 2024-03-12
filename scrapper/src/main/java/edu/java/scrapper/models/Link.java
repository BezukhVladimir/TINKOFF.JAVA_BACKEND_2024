package edu.java.scrapper.models;

import java.time.OffsetDateTime;

public record Link(
    Long id,
    String url,
    OffsetDateTime lastUpdate
) {
}
