package edu.java.bot.api.models.responses;

import java.net.URI;


public record LinkResponse(
    Long id,
    URI url
) {}
