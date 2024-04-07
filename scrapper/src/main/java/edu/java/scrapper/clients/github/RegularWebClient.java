package edu.java.scrapper.clients.github;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import edu.java.scrapper.configurations.retry_policies.RetryPolicy;
import edu.java.scrapper.configurations.retry_policies.RetryPolicyConfig;
import edu.java.scrapper.configurations.retry_policies.RetryPolicySettings;
import edu.java.scrapper.dto.github.Response;
import io.github.resilience4j.retry.Retry;
import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
public class RegularWebClient implements Client {
    @Value("${api.github.baseUrl}")
    private String baseUrl;
    private final WebClient webClient;

    private Retry retry4j;
    @Value(value = "${api.github.retryPolicy}")
    private RetryPolicy policy;
    @Value(value = "${api.github.retryCount}")
    private int count;
    @Value("${api.github.codes}")
    private Set<HttpStatus> statuses;

    public RegularWebClient() {
        this.webClient = WebClient.builder().baseUrl(baseUrl).build();
    }

    public RegularWebClient(@Value("${api.github.baseUrl}") String baseUrl) {
        String finalBaseUrl = baseUrl;

        if (baseUrl.isEmpty()) {
            finalBaseUrl = this.baseUrl;
        }

        this.webClient = WebClient
            .builder()
            .baseUrl(finalBaseUrl)
            .filter(logRequest())
            .build();
    }

    @PostConstruct
    private void configRetry() {
        RetryPolicySettings retryPolicySettings = new RetryPolicySettings()
            .setPolicy(policy)
            .setCount(count)
            .setStatuses(statuses);

        retry4j = RetryPolicyConfig.config(retryPolicySettings);
    }

    private static ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            log.info("Request: {} {}", clientRequest.method(), clientRequest.url());
            clientRequest.headers().forEach((name, values) -> values.forEach(value -> log.info("{}={}", name, value)));
            return Mono.just(clientRequest);
        });
    }

    @Override
    public Response fetchLatestModified(String authorName, String repositoryName) {
        String requestUrl = String.format("networks/%s/%s/events", authorName, repositoryName);

        return webClient.get()
            .uri(uriBuilder -> uriBuilder
                .path(requestUrl)
                .queryParam("per_page", 1)
                .build())
            .retrieve()
            .bodyToMono(String.class)
            .mapNotNull(this::parse)
            .block();
    }

    @Override
    public Response retryFetchLatestModified(String authorName, String repositoryName) {
        return Retry.decorateSupplier(retry4j, () -> fetchLatestModified(authorName, repositoryName)).get();
    }

    private Response parse(String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        try {
            List<Response> responses = objectMapper.readValue(json, new TypeReference<>(){});
            return responses.get(0);
        } catch (Exception e) {
            return null;
        }
    }
}
