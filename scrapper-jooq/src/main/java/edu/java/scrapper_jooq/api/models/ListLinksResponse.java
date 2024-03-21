package edu.java.scrapper_jooq.api.models;

import java.util.List;


public record ListLinksResponse(
    List<LinkResponse> links,
    int size
) {}
