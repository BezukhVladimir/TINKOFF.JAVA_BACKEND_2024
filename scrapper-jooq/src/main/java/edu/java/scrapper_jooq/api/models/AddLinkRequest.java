package edu.java.scrapper_jooq.api.models;

import jakarta.validation.constraints.NotNull;
import java.net.URI;


public record AddLinkRequest(
    @NotNull URI link
) {}

