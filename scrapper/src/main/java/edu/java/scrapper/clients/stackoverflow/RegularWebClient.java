package edu.java.scrapper.clients.stackoverflow;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import edu.java.scrapper.dto.stackoverflow.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
public class RegularWebClient implements Client {
    @Value("${api.stackoverflow.baseUrl}")
    private String baseUrl;
    private final WebClient webClient;

    public RegularWebClient() {
        this.webClient = WebClient.builder().baseUrl(baseUrl).build();
    }

    public RegularWebClient(@Value("${api.stackoverflow.baseUrl}") String baseUrl) {
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

    private static ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            log.info("Request: {} {}", clientRequest.method(), clientRequest.url());
            clientRequest.headers().forEach((name, values) -> values.forEach(value -> log.info("{}={}", name, value)));
            return Mono.just(clientRequest);
        });
    }

    @Override
    public Response fetchLatestModified(Long questionNumber) {
        String requestUrl = String.format("questions/%d/answers", questionNumber);

        return webClient.get()
            .uri(uriBuilder -> uriBuilder
                .path(requestUrl)
                .queryParam("pagesize", 1)
                .queryParam("order", "desc")
                .queryParam("sort", "activity")
                .queryParam("site", "stackoverflow")
                .build()
            )
            .retrieve()
            .bodyToMono(String.class)
            .mapNotNull(this::parse)
            .block();
    }

    private Response parse(String json) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());

            JsonNode rootNode = objectMapper.readTree(json);
            JsonNode itemsNode = rootNode.get("items");
            JsonNode firstItemNode = itemsNode.get(0);
            return objectMapper.treeToValue(firstItemNode, Response.class);
        } catch (Exception e) {
            return null;
        }
    }
}
