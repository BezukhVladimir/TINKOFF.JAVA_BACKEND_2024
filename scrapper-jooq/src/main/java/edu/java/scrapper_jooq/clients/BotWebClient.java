package edu.java.scrapper_jooq.clients;

import edu.java.scrapper_jooq.api.models.ApiErrorResponse;
import edu.java.scrapper_jooq.api.models.LinkUpdateRequest;
import edu.java.scrapper_jooq.exceptions.ApiErrorException;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


public class BotWebClient {

    private final WebClient webClient;

    public BotWebClient(String baseUrl) {
        this.webClient = WebClient.builder().baseUrl(baseUrl).build();
    }

    public String sendUpdate(LinkUpdateRequest request) {
        return webClient
            .post()
            .uri("/updates")
            .body(BodyInserters.fromValue(request))
            .retrieve()
            .onStatus(HttpStatus.BAD_REQUEST::equals,
                response -> response
                    .bodyToMono(ApiErrorResponse.class)
                    .flatMap(errorResponse -> Mono.error(new ApiErrorException(errorResponse))))
            .bodyToMono(String.class)
            .block();
    }
}
