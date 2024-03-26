package edu.java.scrapper.api.models.response;

import java.util.List;


public record ListLinksResponse(
    List<LinkResponse> links,
    int size
) {}
