package edu.java.scrapper.models;

import java.net.URI;
import java.time.OffsetDateTime;

public record Link(
    Long id,
    URI url,
    OffsetDateTime lastUpdate
) {
}
