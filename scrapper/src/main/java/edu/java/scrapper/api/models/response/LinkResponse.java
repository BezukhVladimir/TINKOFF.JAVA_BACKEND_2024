package edu.java.scrapper.api.models.response;

import java.net.URI;


public record LinkResponse(
    Long id,
    URI url
) {}
