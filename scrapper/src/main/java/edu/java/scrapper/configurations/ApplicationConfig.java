package edu.java.scrapper.configurations;

import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import org.jooq.conf.RenderQuotedNames;
import org.jooq.impl.DefaultConfiguration;
import org.springframework.boot.autoconfigure.jooq.DefaultConfigurationCustomizer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app", ignoreUnknownFields = false)
public record ApplicationConfig(
    @NotNull
    Scheduler scheduler,
    @NotNull
    String databaseAccessType,
    @NotNull
    Boolean useQueue,
    @NotNull
    Kafka kafka
) {
    @Bean
    public DefaultConfigurationCustomizer configurationCustomizer() {
        return (DefaultConfiguration c) -> c.settings()
            .withRenderQuotedNames(
                RenderQuotedNames.EXPLICIT_DEFAULT_UNQUOTED
            );
    }

    public record Scheduler(
        boolean enable,
        @NotNull Duration interval,
        @NotNull Duration forceCheckDelay,
        @NotNull Duration removeUnusedLinksInterval
    ) {
    }

    public record Kafka(
        String bootstrapServers,
        Producer producer,
        String topicName
    ) {
        public record Producer(String keySerializer, String valueSerializer) {}
    }
}
