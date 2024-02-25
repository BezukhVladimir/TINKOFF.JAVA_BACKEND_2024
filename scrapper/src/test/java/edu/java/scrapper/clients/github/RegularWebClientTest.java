package edu.java.scrapper.clients.github;

import edu.java.scrapper.clients.AbstractWireMockTest;
import edu.java.scrapper.dto.github.Response;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.util.UriComponentsBuilder;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;

public class RegularWebClientTest extends AbstractWireMockTest {
    private Client client;

    @BeforeEach
    void setUp() {
        String baseUrl = "http://localhost:" + wireMockServer.port();
        client = new RegularWebClient(baseUrl);
    }

    @Test
    public void fetchLatestModified() {
        // Arrange
        String authorName = "octocat";
        String repositoryName = "Hello-World";
        String responseBody =
                """
                [
                  {
                    "id": "22249084964",
                    "type": "PushEvent",
                    "actor": {
                      "id": 583231,
                      "login": "octocat",
                      "display_login": "octocat",
                      "gravatar_id": "",
                      "url": "https://api.github.com/users/octocat",
                      "avatar_url": "https://avatars.githubusercontent.com/u/583231?v=4"
                    },
                    "repo": {
                      "id": 1296269,
                      "name": "octocat/Hello-World",
                      "url": "https://api.github.com/repos/octocat/Hello-World"
                    },
                    "payload": {
                      "push_id": 10115855396,
                      "size": 1,
                      "distinct_size": 1,
                      "ref": "refs/heads/master",
                      "head": "7a8f3ac80e2ad2f6842cb86f576d4bfe2c03e300",
                      "before": "883efe034920928c47fe18598c01249d1a9fdabd",
                      "commits": [
                        {
                          "sha": "7a8f3ac80e2ad2f6842cb86f576d4bfe2c03e300",
                          "author": {
                            "email": "octocat@github.com",
                            "name": "Monalisa Octocat"
                          },
                          "message": "commit",
                          "distinct": true,
                          "url": "https://api.github.com/repos/octocat/Hello-World/commits/7a8f3ac80e2ad2f6842cb86f576d4bfe2c03e300"
                        }
                      ]
                    },
                    "public": true,
                    "created_at": "2022-06-09T12:47:28Z"
                  }
                ]
                """;

        Long expectedId = 22249084964L;
        String expectedType = "PushEvent";
        String expectedActorDisplayLogin = "octocat";
        String expectedRepoName = "octocat/Hello-World";
        OffsetDateTime expectedCreatedAt = OffsetDateTime.parse("2022-06-09T12:47:28Z");

        var ucb = UriComponentsBuilder
            .fromPath("/networks/{authorName}/{repositoryName}/events")
            .queryParam("per_page", 1)
            .uriVariables(Map.of("authorName", authorName, "repositoryName", repositoryName));

        wireMockServer.stubFor(get(urlEqualTo(ucb.toUriString()))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(responseBody)
            )
        );

        // Act
        Response actualResponse = client.fetchLatestModified(authorName, repositoryName).get();

        // Assert
        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse.id()).isEqualTo(expectedId);
        assertThat(actualResponse.type()).isEqualTo(expectedType);
        assertThat(actualResponse.actor().displayLogin()).isEqualTo(expectedActorDisplayLogin);
        assertThat(actualResponse.repo().name()).isEqualTo(expectedRepoName);
        assertThat(actualResponse.createdAt()).isEqualTo(expectedCreatedAt);
    }

    @Test
    public void fetchLatestModifiedEmptyBody() {
        // Arrange
        String authorName = "octocat";
        String repositoryName = "Hello-World";
        String responseBody = "[]";

        var ucb = UriComponentsBuilder
            .fromPath("/networks/{authorName}/{repositoryName}/events")
            .queryParam("per_page", 1)
            .uriVariables(Map.of("authorName", authorName, "repositoryName", repositoryName));

        wireMockServer.stubFor(get(urlEqualTo(ucb.toUriString()))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(responseBody)
            )
        );

        // Act
        Optional<Response> actualResponse = client.fetchLatestModified(authorName, repositoryName);

        // Assert
        assertThat(actualResponse).isNotPresent();
    }

    @Test
    public void fetchLatestModifiedInvalidBody() {
        // Arrange
        String authorName = "octocat";
        String repositoryName = "Hello-World";
        String responseBody = "invalid body";

        var ucb = UriComponentsBuilder
            .fromPath("/networks/{authorName}/{repositoryName}/events")
            .queryParam("per_page", 1)
            .uriVariables(Map.of("authorName", authorName, "repositoryName", repositoryName));

        wireMockServer.stubFor(get(urlEqualTo(ucb.toUriString()))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(responseBody)
            )
        );

        // Act
        Optional<Response> actualResponse = client.fetchLatestModified(authorName, repositoryName);

        // Assert
        assertThat(actualResponse).isNotPresent();
    }
}
