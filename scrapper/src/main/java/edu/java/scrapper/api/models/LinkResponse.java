package edu.java.scrapper.api.models;

import java.net.URI;


public record LinkResponse(
    Long id,
    URI url
) {}
