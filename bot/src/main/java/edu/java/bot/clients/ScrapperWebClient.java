package edu.java.bot.clients;

import edu.java.bot.api.models.requests.AddLinkRequest;
import edu.java.bot.api.models.requests.RemoveLinkRequest;
import edu.java.bot.api.models.responses.ApiErrorResponse;
import edu.java.bot.api.models.responses.LinkResponse;
import edu.java.bot.api.models.responses.ListLinksResponse;
import edu.java.bot.exceptions.ApiErrorException;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


public class ScrapperWebClient {
    private final WebClient webClient;

    public ScrapperWebClient(String baseUrl) {
        this.webClient = WebClient.builder().baseUrl(baseUrl).build();
    }

    public String registerChat(Long chatId) {
        return webClient
            .post()
            .uri(uriBuilder -> uriBuilder.path(PATH_TO_CHAT).build(chatId))
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError,
                response -> response
                    .bodyToMono(ApiErrorResponse.class)
                    .flatMap(errorResponse -> Mono.error(new ApiErrorException(errorResponse))))
            .bodyToMono(String.class)
            .block();
    }

    public String deleteChat(Long chatId) {
        return webClient
            .delete()
            .uri(uriBuilder -> uriBuilder.path(PATH_TO_CHAT).build(chatId))
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError,
                response -> response
                    .bodyToMono(ApiErrorResponse.class)
                    .flatMap(errorResponse -> Mono.error(new ApiErrorException(errorResponse))))
            .bodyToMono(String.class)
            .block();
    }

    public ListLinksResponse getLinks(Long chatId) {
        return webClient
            .get()
            .uri(PATH_TO_LINK)
            .header(HEADER_NAME, String.valueOf(chatId))
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError,
                response -> response
                    .bodyToMono(ApiErrorResponse.class)
                    .flatMap(errorResponse -> Mono.error(new ApiErrorException(errorResponse))))
            .bodyToMono(ListLinksResponse.class)
            .block();
    }

    public LinkResponse addLink(Long chatId, AddLinkRequest request) {
        return webClient
            .post()
            .uri(PATH_TO_LINK)
            .header(HEADER_NAME, String.valueOf(chatId))
            .body(BodyInserters.fromValue(request))
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError,
                response -> response
                    .bodyToMono(ApiErrorResponse.class)
                    .flatMap(errorResponse -> Mono.error(new ApiErrorException(errorResponse))))
            .bodyToMono(LinkResponse.class)
            .block();
    }

    public LinkResponse removeLink(Long chatId, RemoveLinkRequest request) {
        return webClient.method(HttpMethod.DELETE)
            .uri(PATH_TO_LINK)
            .header(HEADER_NAME, String.valueOf(chatId))
            .body(BodyInserters.fromValue(request))
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError,
                response -> response
                    .bodyToMono(ApiErrorResponse.class)
                    .flatMap(errorResponse -> Mono.error(new ApiErrorException(errorResponse))))
            .bodyToMono(LinkResponse.class)
            .block();
    }

    private static final String PATH_TO_CHAT = "tg-chat/{id}";
    private static final String PATH_TO_LINK = "/links";
    private static final String HEADER_NAME = "Tg-Chat-Id";
}
