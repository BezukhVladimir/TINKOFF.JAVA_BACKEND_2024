package edu.java.scrapper.api.models.request;

import jakarta.validation.constraints.NotNull;
import java.net.URI;


public record RemoveLinkRequest(
    @NotNull URI link
) {}
