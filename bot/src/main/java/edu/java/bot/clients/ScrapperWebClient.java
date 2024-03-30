package edu.java.bot.clients;

import edu.java.bot.api.models.requests.AddLinkRequest;
import edu.java.bot.api.models.requests.RemoveLinkRequest;
import edu.java.bot.api.models.responses.ApiErrorResponse;
import edu.java.bot.api.models.responses.LinkResponse;
import edu.java.bot.api.models.responses.ListLinksResponse;
import edu.java.bot.configurations.RetryPolicyConfig;
import edu.java.bot.exceptions.ApiErrorException;
import edu.java.bot.models.RetryPolicy;
import edu.java.bot.models.RetryPolicySettings;
import io.github.resilience4j.retry.Retry;
import jakarta.annotation.PostConstruct;
import java.util.Set;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class ScrapperWebClient {
    private final WebClient webClient;

    private Retry retry4j;
    @Value(value = "${api.scrapper.retryPolicy}")
    private RetryPolicy policy;
    @Value(value = "${api.scrapper.retryCount}")
    private int count;
    @Value("#{'${api.scrapper.codes}'.split(',')}")
    private Set<HttpStatus> statuses;

    public ScrapperWebClient(String baseUrl) {
        this.webClient = WebClient.builder().baseUrl(baseUrl).build();
    }

    @PostConstruct
    private void configRetry() {
        RetryPolicySettings retryPolicySettings = new RetryPolicySettings()
            .setPolicy(policy)
            .setCount(count)
            .setStatuses(statuses);

        retry4j = RetryPolicyConfig.config(retryPolicySettings);
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

    public String retryRegisterChat(Long chatId) {
        return Retry.decorateSupplier(retry4j, () -> registerChat(chatId)).get();
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

    public String retryDeleteChat(Long chatId) {
        return Retry.decorateSupplier(retry4j, () -> deleteChat(chatId)).get();
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

    public ListLinksResponse retryGetLinks(Long chatId) {
        return Retry.decorateSupplier(retry4j, () -> getLinks(chatId)).get();
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

    public LinkResponse retryAddLink(Long chatId, AddLinkRequest request) {
        return Retry.decorateSupplier(retry4j, () -> addLink(chatId, request)).get();
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

    public LinkResponse retryRemoveLink(Long chatId, RemoveLinkRequest request) {
        return Retry.decorateSupplier(retry4j, () -> removeLink(chatId, request)).get();
    }

    private static final String PATH_TO_CHAT = "tg-chat/{id}";
    private static final String PATH_TO_LINK = "/links";
    private static final String HEADER_NAME = "Tg-Chat-Id";
}
