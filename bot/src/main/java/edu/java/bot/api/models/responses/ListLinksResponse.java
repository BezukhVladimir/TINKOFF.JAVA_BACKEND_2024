package edu.java.bot.api.models.responses;

import java.util.List;


public record ListLinksResponse(
    List<LinkResponse> links,
    int size
) {}
