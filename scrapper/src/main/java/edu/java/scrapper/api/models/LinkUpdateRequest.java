package edu.java.scrapper.api.models;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.util.List;

public record LinkUpdateRequest(
    @NotNull Long id,
    @NotNull URI url,
    @NotNull @NotEmpty String description,
    @NotNull @NotEmpty List<Long> tgChatIds
) {}
