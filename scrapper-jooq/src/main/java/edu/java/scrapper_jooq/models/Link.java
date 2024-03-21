package edu.java.scrapper_jooq.models;

import java.net.URI;
import java.time.OffsetDateTime;

public record Link(
    Long id,
    URI url,
    OffsetDateTime lastUpdate
) {
}
