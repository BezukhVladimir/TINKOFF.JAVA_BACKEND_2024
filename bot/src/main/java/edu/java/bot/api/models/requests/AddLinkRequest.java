package edu.java.bot.api.models.requests;

import jakarta.validation.constraints.NotNull;
import java.net.URI;


public record AddLinkRequest(
    @NotNull URI link
) {}

