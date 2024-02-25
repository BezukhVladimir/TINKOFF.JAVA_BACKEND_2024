package edu.java.scrapper.clients.stackoverflow;

import edu.java.scrapper.clients.AbstractWireMockTest;
import edu.java.scrapper.dto.stackoverflow.Response;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
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
    void fetchLatestModified() {
        // Arrange
        Long questionId = 1732348L;
        String responseBody = """
            {
              "items": [
                {
                  "owner": {
                    "account_id": 29000684,
                    "reputation": 21,
                    "user_id": 22214278,
                    "user_type": "registered",
                    "profile_image": "https://i.stack.imgur.com/T9AsA.jpg?s=256&g=1",
                    "display_name": "Charlotte Briggs",
                    "link": "https://stackoverflow.com/users/22214278/charlotte-briggs"
                  },
                  "is_accepted": false,
                  "community_owned_date": 1707758657,
                  "score": 0,
                  "last_activity_date": 1707758657,
                  "creation_date": 1707758657,
                  "answer_id": 77983233,
                  "question_id": 1732348,
                  "content_license": "CC BY-SA 4.0"
                }
              ],
              "has_more": true,
              "quota_max": 10000,
              "quota_remaining": 9912
            }
            """;

        String expectedOwnerDisplayName = "Charlotte Briggs";
        OffsetDateTime expectedLastActivityDate = Instant.ofEpochSecond(1707758657L).atOffset(ZoneOffset.UTC);
        Long expectedAnswerId = 77983233L;
        Long expectedQuestionId = 1732348L;

        var ucb = UriComponentsBuilder
            .fromPath("/questions/{questionId}/answers")
            .queryParam("pagesize", 1)
            .queryParam("order", "desc")
            .queryParam("sort", "activity")
            .queryParam("site", "stackoverflow")
            .uriVariables(Map.of("questionId", questionId));

        wireMockServer.stubFor(get(urlEqualTo(ucb.toUriString()))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(responseBody)
            )
        );

        // Act
        Response actualResponse = client.fetchLatestModified(questionId).get();

        // Assert
        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse.owner().displayName()).isEqualTo(expectedOwnerDisplayName);
        assertThat(actualResponse.lastActivityDate()).isEqualTo(expectedLastActivityDate);
        assertThat(actualResponse.answerId()).isEqualTo(expectedAnswerId);
        assertThat(actualResponse.questionId()).isEqualTo(expectedQuestionId);
    }

    @Test
    void fetchLatestModifiedEmptyBody() {
        // Arrange
        Long questionId = 1732348L;
        String responseBody = """
            {
              "items": [],
              "has_more":,
              "quota_max":,
              "quota_remaining":
            }
            """;

        var ucb = UriComponentsBuilder
            .fromPath("/questions/{questionId}/answers")
            .queryParam("pagesize", 1)
            .queryParam("order", "desc")
            .queryParam("sort", "activity")
            .queryParam("site", "stackoverflow")
            .uriVariables(Map.of("questionId", questionId));

        wireMockServer.stubFor(get(urlEqualTo(ucb.toUriString()))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(responseBody)
            )
        );

        // Act
        Optional<Response> actualResponse = client.fetchLatestModified(questionId);

        // Assert
        assertThat(actualResponse).isNotPresent();
    }

    @Test
    void fetchLatestModifiedInvalidBody() {
        // Arrange
        Long questionId = 1732348L;
        String responseBody = "invalid body";

        var ucb = UriComponentsBuilder
            .fromPath("/questions/{questionId}/answers")
            .queryParam("pagesize", 1)
            .queryParam("order", "desc")
            .queryParam("sort", "activity")
            .queryParam("site", "stackoverflow")
            .uriVariables(Map.of("questionId", questionId));

        wireMockServer.stubFor(get(urlEqualTo(ucb.toUriString()))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(responseBody)
            )
        );

        // Act
        Optional<Response> actualResponse = client.fetchLatestModified(questionId);

        // Assert
        assertThat(actualResponse).isNotPresent();
    }
}
