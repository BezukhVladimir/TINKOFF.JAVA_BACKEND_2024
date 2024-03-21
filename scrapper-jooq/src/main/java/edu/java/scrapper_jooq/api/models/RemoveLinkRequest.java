package edu.java.scrapper_jooq.api.models;

import jakarta.validation.constraints.NotNull;
import java.net.URI;


public record RemoveLinkRequest(
    @NotNull URI link
) {}
