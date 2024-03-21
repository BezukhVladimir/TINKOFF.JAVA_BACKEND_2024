package edu.java.scrapper_jooq.api.models;

import java.net.URI;


public record LinkResponse(
    Long id,
    URI url
) {}
