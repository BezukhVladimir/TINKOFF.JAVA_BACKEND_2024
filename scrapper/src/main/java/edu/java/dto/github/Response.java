// https://docs.github.com/ru/rest/activity/events?apiVersion=2022-11-28#list-public-events-for-a-network-of-repositories

package edu.java.dto.github;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Response(
    Long id,
    String type,
    Actor actor,
    Repo repo,
    @JsonProperty("created_at")
    OffsetDateTime createdAt
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Actor(
        @JsonProperty("display_login")
        String displayLogin
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Repo(
        String name
    ) {}
}
