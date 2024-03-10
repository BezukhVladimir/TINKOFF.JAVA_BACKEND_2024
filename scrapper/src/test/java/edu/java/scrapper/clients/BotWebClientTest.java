package edu.java.scrapper.clients;


import edu.java.scrapper.api.models.ApiErrorResponse;
import edu.java.scrapper.api.models.LinkUpdateRequest;
import edu.java.scrapper.exceptions.ApiErrorException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowableOfType;

public class BotWebClientTest extends AbstractWireMockTest {
    private BotWebClient botWebClient;

    @BeforeEach
    void setUp() {
        String baseUrl = "http://localhost:" + wireMockServer.port();
        botWebClient = new BotWebClient(baseUrl);
    }

    @Test
    public void correctBody() throws URISyntaxException {
        // Arrange
        String responseBody = "Обновление обработано";
        LinkUpdateRequest request = new LinkUpdateRequest(
            1L,
            new URI("1"),
            "1",
            List.of(1L)
        );

        wireMockServer.stubFor(post(urlEqualTo("/updates"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(responseBody)));

        // Act
        String actualResponse = botWebClient.sendUpdate(request);

        // Assert
        assertThat(actualResponse).isEqualTo(responseBody);
    }

    @Test
    public void invalidBody() {
        // Arrange
        String responseBody = """
                {
                    "description":"123",
                    "code":"400",
                    "exceptionName":"123",
                    "exceptionMessage":"123",
                    "stackTrace":[
                        "1",
                        "2",
                        "3"
                    ]
                }

            """;
        LinkUpdateRequest request = new LinkUpdateRequest(
            1L,
            null,
            "1",
            List.of()
        );

        String expectedDescription = "123";
        String expectedCode = "400";
        String expectedName = "123";
        String expectedMessage = "123";

        wireMockServer.stubFor(post(urlEqualTo("/updates"))
            .willReturn(aResponse()
                .withStatus(400)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(responseBody)));

        // Act
        ApiErrorException thrownException = catchThrowableOfType(
            () -> botWebClient.sendUpdate(request),
            ApiErrorException.class
        );
        ApiErrorResponse actualResponse = thrownException.getErrorResponse();

        // Assert
        assertThat(actualResponse.description()).isEqualTo(expectedDescription);
        assertThat(actualResponse.code()).isEqualTo(expectedCode);
        assertThat(actualResponse.exceptionName()).isEqualTo(expectedName);
        assertThat(actualResponse.exceptionMessage()).isEqualTo(expectedMessage);
    }
}
