package edu.java.scrapper.configurations;

import edu.java.scrapper.models.RetryPolicySettings;
import io.github.resilience4j.core.IntervalFunction;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import java.time.Duration;
import java.time.OffsetDateTime;
import org.springframework.web.reactive.function.client.WebClientResponseException;


public class RetryPolicyConfig {
    private static final int INTERVAL = 3;

    private RetryPolicyConfig() {
    }

    public static Retry config(RetryPolicySettings settings) {
        RetryConfig config = switch (settings.getPolicy()) {
            case CONSTANT    -> constant(settings);
            case LINEAR      -> linear(settings);
            case EXPONENTIAL -> exponential(settings);
        };

        return Retry.of("scrapper-retry-" + OffsetDateTime.now(), config);
    }

    private static RetryConfig constant(RetryPolicySettings settings) {
        return RetryConfig.<WebClientResponseException>custom()
            .maxAttempts(settings.getCount())
            .waitDuration(Duration.ofSeconds(INTERVAL))
            .retryOnResult(response -> settings.getStatuses().contains(response.getStatusCode()))
            .build();
    }

    private static RetryConfig linear(RetryPolicySettings settings) {
        return RetryConfig.<WebClientResponseException>custom()
            .maxAttempts(settings.getCount())
            .intervalFunction(IntervalFunction.of(
                Duration.ofSeconds(INTERVAL),
                attempt -> INTERVAL + attempt * INTERVAL
            ))
            .retryOnResult(response -> settings.getStatuses().contains(response.getStatusCode()))
            .build();
    }

    private static RetryConfig exponential(RetryPolicySettings settings) {
        return RetryConfig.<WebClientResponseException>custom()
            .maxAttempts(settings.getCount())
            .intervalFunction(IntervalFunction.ofExponentialBackoff(
                IntervalFunction.DEFAULT_INITIAL_INTERVAL,
                IntervalFunction.DEFAULT_MULTIPLIER
            ))
            .retryOnResult(response -> settings.getStatuses().contains(response.getStatusCode()))
            .build();
    }
}
