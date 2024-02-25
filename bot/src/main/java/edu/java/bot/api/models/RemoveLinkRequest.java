package edu.java.bot.api.models;

import jakarta.validation.constraints.NotNull;
import java.net.URI;


public record RemoveLinkRequest(
    @NotNull URI link
) {}
