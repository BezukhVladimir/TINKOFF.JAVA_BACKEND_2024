package edu.java.scrapper.clients.stackoverflow;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import edu.java.clients.stackoverflow.Client;
import edu.java.clients.stackoverflow.RegularWebClient;
import edu.java.dto.stackoverflow.Response;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;

public class RegularWebClientTest {
    private WireMockServer wireMockServer;
    private Client client;

    @BeforeEach
    void setUp() {
        wireMockServer = new WireMockServer();
        wireMockServer.start();
        WireMock.configureFor("localhost", wireMockServer.port());
        String baseUrl = "http://localhost:" + wireMockServer.port();
        client = new RegularWebClient(baseUrl);
    }

    @AfterEach
    void tearDown() {
        wireMockServer.stop();
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

        wireMockServer.stubFor(get(urlEqualTo(String.format("/questions/%d/answers"
            + "?pagesize=1"
            + "&order=desc"
            + "&sort=activity"
            + "&site=stackoverflow", questionId)))
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
        assertThat(actualResponse.getOwner().getDisplayName()).isEqualTo(expectedOwnerDisplayName);
        assertThat(actualResponse.getLastActivityDate()).isEqualTo(expectedLastActivityDate);
        assertThat(actualResponse.getAnswerId()).isEqualTo(expectedAnswerId);
        assertThat(actualResponse.getQuestionId()).isEqualTo(expectedQuestionId);
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

        wireMockServer.stubFor(get(urlEqualTo(String.format("/questions/%d/answers"
            + "?pagesize=1"
            + "&order=desc"
            + "&sort=activity"
            + "&site=stackoverflow", questionId)))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(responseBody)
            )
        );

        // Act
        Optional<Response> actualResponse = client.fetchLatestModified(questionId);

        // Assert
        assertThat(actualResponse.isPresent()).isFalse();
    }

    @Test
    void fetchLatestModifiedInvalidBody() {
        // Arrange
        Long questionId = 1732348L;
        String responseBody = "invalid body";

        wireMockServer.stubFor(get(urlEqualTo(String.format("/questions/%d/answers"
            + "?pagesize=1"
            + "&order=desc"
            + "&sort=activity"
            + "&site=stackoverflow", questionId)))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(responseBody)
            )
        );

        // Act
        Optional<Response> actualResponse = client.fetchLatestModified(questionId);

        // Assert
        assertThat(actualResponse.isPresent()).isFalse();
    }
}