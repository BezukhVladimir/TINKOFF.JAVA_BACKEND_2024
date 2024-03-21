package edu.java.scrapper.configurations.clients;

import edu.java.scrapper.clients.BotWebClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class BotWebConfig {
    @Value("${api.bot.baseUrl}")
    public String baseUrl;

    @Bean
    public BotWebClient botWebClient() {
        return new BotWebClient(baseUrl);
    }
}
