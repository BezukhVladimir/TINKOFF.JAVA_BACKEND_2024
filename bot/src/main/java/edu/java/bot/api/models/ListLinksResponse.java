package edu.java.bot.api.models;

import java.util.List;


public record ListLinksResponse(
    List<LinkResponse> links,
    int size
) {}
