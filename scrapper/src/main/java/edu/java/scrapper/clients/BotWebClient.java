package edu.java.scrapper.clients;

import edu.java.scrapper.api.models.request.LinkUpdateRequest;
import edu.java.scrapper.api.models.response.ApiErrorResponse;
import edu.java.scrapper.configurations.retry_policies.RetryPolicy;
import edu.java.scrapper.configurations.retry_policies.RetryPolicyConfig;
import edu.java.scrapper.configurations.retry_policies.RetryPolicySettings;
import edu.java.scrapper.exceptions.ApiErrorException;
import io.github.resilience4j.retry.Retry;
import jakarta.annotation.PostConstruct;
import java.util.Set;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class BotWebClient {
    private final WebClient webClient;

    private Retry retry4j;
    @Value(value = "${api.bot.retryPolicy}")
    private RetryPolicy policy;
    @Value(value = "${api.bot.retryCount}")
    private int count;
    @Value("${api.bot.codes}")
    private Set<HttpStatus> statuses;

    public BotWebClient(String baseUrl) {
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

    public String retrySendUpdate(LinkUpdateRequest request) {
        return Retry.decorateSupplier(retry4j, () -> sendUpdate(request)).get();
    }
}
