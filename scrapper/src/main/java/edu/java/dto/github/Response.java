// https://docs.github.com/ru/rest/activity/events?apiVersion=2022-11-28#list-public-events-for-a-network-of-repositories

package edu.java.dto.github;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Response {
    private Long id;
    private String type;
    private Actor actor;
    private Repo repo;

    @JsonProperty("created_at")
    private OffsetDateTime createdAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Actor {
        @JsonProperty("display_login")
        private String displayLogin;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Repo {
        @JsonProperty("name")
        private String name;
    }
}
